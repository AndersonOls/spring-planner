package com.anderson.planner.trip;

import com.anderson.planner.activities.ActivityData;
import com.anderson.planner.activities.ActivityRequestPayload;
import com.anderson.planner.activities.ActivityResponse;
import com.anderson.planner.activities.ActivityService;
import com.anderson.planner.activities.exceptions.ActivityDateException;
import com.anderson.planner.link.LinkData;
import com.anderson.planner.link.LinkRequestPayload;
import com.anderson.planner.link.LinkResponse;
import com.anderson.planner.link.LinkService;
import com.anderson.planner.participant.ParticipantCreateResponse;
import com.anderson.planner.participant.ParticipantData;
import com.anderson.planner.participant.ParticipantRequestPayload;
import com.anderson.planner.trip.exceptions.CreateTripDateException;
import com.anderson.planner.participant.ParticipantService;
import com.anderson.planner.trip.exceptions.TripNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TripService {
    @Autowired
    private TripRepository repository;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

    public Trip createAndSaveTrip(TripRequestPayload payload) {
        Trip newTrip = new Trip(payload);

        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(newTrip.getStartsAt())) {
            throw new CreateTripDateException("Date must be in the future");
        }
        if (newTrip.getStartsAt().isAfter(newTrip.getEndsAt())) {
            throw new CreateTripDateException("End date must be after start date");
        }

        this.repository.save(newTrip);

        participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return newTrip;
    }

    public Trip getTripDetail(UUID id){
        Optional<Trip> trip = this.repository.findById(id);
        return trip.orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + id));
    }

    public Trip updateTrip(UUID id, TripRequestPayload payload) {
        Optional<Trip> trip = this.repository.findById(id);
        if (trip.isPresent()) {
            Trip rawtrip = trip.get();

            if (payload.ends_at() == null || payload.starts_at() == null || payload.destination() == null || payload.destination().trim().isEmpty()) {
                throw new CreateTripDateException("All fields must be provided and not be empty");
            }

            LocalDateTime startsAt;
            LocalDateTime endsAt;

            try {
                startsAt = LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
                endsAt = LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME);
            } catch (DateTimeParseException e) {
                throw new CreateTripDateException("Invalid date format");
            }

            rawtrip.setStartsAt(startsAt);
            rawtrip.setEndsAt(endsAt);
            rawtrip.setDestination(payload.destination());

            var currentDate = LocalDateTime.now();

            if (currentDate.isAfter(rawtrip.getStartsAt())) {
                throw new CreateTripDateException("Start date must be in the future");
            }
            if (rawtrip.getStartsAt().isAfter(rawtrip.getEndsAt())) {
                throw new CreateTripDateException("End date must be after start date");
            }

            this.repository.save(rawtrip);

            return rawtrip;
        } else {
            throw new CreateTripDateException("Trip not found with id: " + id);
        }
    }


    public Trip confirmTrip(UUID id) {
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rawtrip = trip.get();
            rawtrip.setIsConfirmed(true);
            this.repository.save(rawtrip);
            this.participantService.triggerConfirmationEmailToParticipants(id);
            return rawtrip;
        }

        throw new TripNotFoundException("Trip not found with id: " + id);

    }

    public ActivityResponse registerActivity(ActivityRequestPayload payload, UUID id) {
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rawtrip = trip.get();

            var dateTime = LocalDateTime.parse(payload.occurs_at(), DateTimeFormatter.ISO_DATE_TIME);

            if(dateTime.isBefore(rawtrip.getStartsAt()) || dateTime.isAfter(rawtrip.getEndsAt())){
                throw new ActivityDateException("Activity date is not valid, it should be between " + rawtrip.getStartsAt() + " and " + rawtrip.getEndsAt());
            }
            return this.activityService.resgisterActivity(payload, rawtrip);

        }

        throw new TripNotFoundException("Trip not found with id: " + id);
    }

    public List<ActivityData> getAllActivitiesFromId(UUID id) {
        Optional<Trip> trip = this.repository.findById(id);
        if(trip.isEmpty()){
            throw new TripNotFoundException("Trip not found with id: " + id);
        }
        return this.activityService.getAllActivitiesFromId(id);
    }

    public ParticipantCreateResponse inviteParticipant(ParticipantRequestPayload payload, UUID id) {
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rawtrip = trip.get();

            ParticipantCreateResponse participantResponse = this.participantService.registerParticipantToEvent(payload.email(), rawtrip);

            if (rawtrip.getIsConfirmed()) {
                this.participantService.triggerConfirmationEmailToParticipant(payload.email());
            }
            return participantResponse;
        }

        throw new TripNotFoundException("Trip not found with id: " + id);
    }

    public List<ParticipantData> getAllParticipantsFromId(UUID id) {
        Optional<Trip> trip = this.repository.findById(id);
        if (trip.isEmpty()) {
            throw new TripNotFoundException("Trip not found with id: " + id);
        }
        return this.participantService.getAllParticpantsFromEvent(id);
    }

    public LinkResponse registerLink(LinkRequestPayload payload, UUID id) {
        Optional<Trip> trip = this.repository.findById(id);
        if (trip.isPresent()){
            Trip rawtrip = trip.get();
            return this.linkService.registerLink(payload, rawtrip);
        }
        throw new TripNotFoundException("Trip not found with id: " + id);
    }

    public List<LinkData> getAllLinksFromId(UUID id) {
        Optional<Trip> trip = this.repository.findById(id);
        if (trip.isEmpty()) {
            throw new TripNotFoundException("Trip not found with id: " + id);
        }
        return this.linkService.getAllLinksFromTrip(id);
    }
}
