package com.example.Lifepath.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.Lifepath.dto.ApiDtos.*;
import com.example.Lifepath.entity.*;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UserModulesServiceTest {
    @Test
    void documentChecklistStatusIsStoredForAuthenticatedUser() {
        DocumentRepository docs = mock(DocumentRepository.class);
        OpportunityDocumentRepository links = mock(OpportunityDocumentRepository.class);
        UserDocumentRepository userDocs = mock(UserDocumentRepository.class);
        UserRepository users = mock(UserRepository.class);
        User user = mock(User.class);
        when(user.getId()).thenReturn(12L);
        Document doc = new Document("Demo transcript", "Sample only", null);
        when(docs.findById(4L)).thenReturn(Optional.of(doc));
        when(users.findById(12L)).thenReturn(Optional.of(user));
        when(docs.findAll()).thenReturn(List.of(doc));
        when(links.findAll()).thenReturn(List.of());
        when(userDocs.findAllByUserIdAndDocumentIdOrderByIdAsc(12L, null)).thenReturn(List.of());
        when(userDocs.save(any())).thenAnswer(call -> call.getArgument(0));

        var result = new DocumentService(docs, links, userDocs, users).updateStatus(12L, 4L,
                new DocumentStatusRequest("SUBMITTED"));
        assertEquals("SUBMITTED", result.status());
        verify(userDocs).save(argThat(row -> row.getUser() == user && row.getDocument() == doc));
    }

    @Test
    void userCannotMarkDocumentVerified() {
        DocumentService service = new DocumentService(mock(DocumentRepository.class),
                mock(OpportunityDocumentRepository.class), mock(UserDocumentRepository.class), mock(UserRepository.class));
        ApiException error = assertThrows(ApiException.class,
                () -> service.updateStatus(1L, 2L, new DocumentStatusRequest("VERIFIED")));
        assertEquals(400, error.getStatus().value());
    }

    @Test
    void preparationCompletionIsPersistedPerUser() {
        PreparationTaskRepository tasks = mock(PreparationTaskRepository.class);
        UserPreparationTaskRepository userTasks = mock(UserPreparationTaskRepository.class);
        UserRepository users = mock(UserRepository.class);
        User user = mock(User.class);
        PreparationTask task = new PreparationTask(null, "Demo mock test", "Sample task");
        when(users.findById(5L)).thenReturn(Optional.of(user));
        when(tasks.findById(8L)).thenReturn(Optional.of(task));
        when(userTasks.findByUserIdAndTaskId(5L, 8L)).thenReturn(Optional.empty());
        when(userTasks.save(any())).thenAnswer(call -> call.getArgument(0));
        var result = new PreparationService(tasks, userTasks, users).updateStatus(5L, 8L,
                new PreparationTaskStatusRequest("COMPLETED"));
        assertEquals("COMPLETED", result.status());
        verify(userTasks).save(argThat(row -> row.getUser() == user && row.getTask() == task
                && "COMPLETED".equals(row.getStatus())));
    }

    @Test
    void notificationUpdatesAreRestrictedToOwner() {
        NotificationRepository repository = mock(NotificationRepository.class);
        User owner = mock(User.class), other = mock(User.class);
        when(owner.getId()).thenReturn(1L);
        when(other.getId()).thenReturn(2L);
        Notification notification = new Notification(owner, "Demo reminder");
        when(repository.findById(7L)).thenReturn(Optional.of(notification));
        when(repository.save(any())).thenAnswer(call -> call.getArgument(0));
        NotificationService service = new NotificationService(repository);
        assertThrows(ApiException.class, () -> service.markRead(2L, 7L));
        var view = service.markRead(1L, 7L);
        assertTrue(view.read());
    }

    @Test
    void settingsChangePasswordUsesPasswordEncoderAndChecksCurrentPassword() {
        UserRepository users = mock(UserRepository.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = mock(User.class);
        when(user.getPassword()).thenReturn(encoder.encode("old-password"));
        when(users.findById(3L)).thenReturn(Optional.of(user));
        SettingsService service = new SettingsService(users, encoder);
        assertThrows(ApiException.class, () -> service.changePassword(3L,
                new ChangePasswordRequest("wrong-password", "new-password-123")));
        service.changePassword(3L, new ChangePasswordRequest("old-password", "new-password-123"));
        verify(user).setPassword(argThat(value -> encoder.matches("new-password-123", value)));
    }

    @Test
    void deadlinesIncludePastDueAndUpcomingStates() {
        DeadlineRepository repository = mock(DeadlineRepository.class);
        Opportunity opportunity = mock(Opportunity.class);
        when(opportunity.getId()).thenReturn(2L);
        when(opportunity.getTitle()).thenReturn("Demo opportunity");
        when(repository.findAllByOrderByDeadlineAsc()).thenReturn(List.of(
                new Deadline(opportunity, "Passed", LocalDateTime.now().minusDays(2), null),
                new Deadline(opportunity, "Soon", LocalDateTime.now().plusDays(2), null),
                new Deadline(opportunity, "Later", LocalDateTime.now().plusDays(20), null)));
        var results = new DeadlineService(repository).upcoming();
        assertEquals(List.of("PASSED", "DUE_SOON", "UPCOMING"), results.stream().map(DeadlineView::status).toList());
    }
}
