package com.anderson.planner.activities;

import com.anderson.planner.activities.exceptions.ActivityNotFoundException;
import com.anderson.planner.trip.Trip;
import com.anderson.planner.trip.exceptions.TripNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    public ActivityResponse resgisterActivity(ActivityRequestPayload payload, Trip trip) {
        Activity newActivity = new Activity(payload.title(), payload.occurs_at(),trip);

        this.repository.save(newActivity);

        return new ActivityResponse(newActivity.getId());
    }

    public List<ActivityData> getAllActivitiesFromId(UUID tripId) {
        List<Activity> activities = this.repository.findByTripId(tripId);
        if (activities.isEmpty()) {
            throw new ActivityNotFoundException("This trip has no activities");
        }
        return activities.stream()
                .map(activity -> new ActivityData(activity.getId(),
                        activity.getTitle(),
                        activity.getOccursAt()))
                .collect(Collectors.toList());
    }
}
