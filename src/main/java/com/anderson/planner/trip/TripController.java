package com.anderson.planner.trip;

import com.anderson.planner.activities.ActivityData;
import com.anderson.planner.activities.ActivityRequestPayload;
import com.anderson.planner.activities.ActivityResponse;
import com.anderson.planner.link.LinkData;
import com.anderson.planner.link.LinkRequestPayload;
import com.anderson.planner.link.LinkResponse;
import com.anderson.planner.participant.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private ParticipantService participantService;


    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@Valid @RequestBody TripRequestPayload payload) {
        Trip newTrip = tripService.createAndSaveTrip(payload);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetail(@PathVariable UUID id){
        Trip trip = tripService.getTripDetail(id);

        return ResponseEntity.ok(trip);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@Valid @PathVariable UUID id, @RequestBody TripRequestPayload payload){
        Trip trip = tripService.updateTrip(id, payload);

        return ResponseEntity.ok(trip);
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id){
        Trip trip = tripService.confirmTrip(id);

        return ResponseEntity.ok(trip);
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload){
        ActivityResponse activityResponse = tripService.registerActivity(payload, id);
        return ResponseEntity.ok(activityResponse);
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id){
        List<ActivityData> activityData = tripService.getAllActivitiesFromId(id);
        return ResponseEntity.ok(activityData);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        ParticipantCreateResponse participantCreateResponse = tripService.inviteParticipant(payload, id);
        return ResponseEntity.ok(participantCreateResponse);
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id){
        List<ParticipantData> participantData = tripService.getAllParticipantsFromId(id);
        return ResponseEntity.ok(participantData);
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload){
        LinkResponse linkResponse = tripService.registerLink(payload, id);
        return ResponseEntity.ok(linkResponse);
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id){
        List<LinkData> linkDataList = tripService.getAllLinksFromId(id);
        return ResponseEntity.ok(linkDataList);
    }

}

