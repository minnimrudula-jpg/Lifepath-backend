package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.*;
import com.example.Lifepath.entity.*;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final UserProfileRepository profiles;
    private final UserRepository users;

    public ProfileService(UserProfileRepository p, UserRepository u) {
        profiles = p;
        users = u;
    }

    public ProfileView get(Long userId) {
        return view(profiles.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Profile not found")));
    }

    @Transactional
    public ProfileView update(Long userId, ProfileRequest r) {
        UserProfile p = profiles.findById(userId).orElseGet(() -> new UserProfile(
                users.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"))));
        p.setDateOfBirth(r.dateOfBirth());
        p.setGender(r.gender());
        p.setCategory(r.category());
        p.setAnnualIncome(r.annualIncome());
        p.setEducation(r.education());
        p.setBranch(r.branch());
        p.setExperienceYears(r.experienceYears());
        p.setPhone(r.phone());
        p.setAddress(r.address());
        p.setCity(r.city());
        p.setState(r.state());
        return view(profiles.save(p));
    }

    private ProfileView view(UserProfile p) {
        return new ProfileView(p.getUserId(), p.getDateOfBirth(), p.getGender(), p.getCategory(), p.getAnnualIncome(),
                p.getEducation(), p.getBranch(), p.getExperienceYears(), p.getPhone(), p.getAddress(), p.getCity(),
                p.getState());
    }
}
