package com.anderson.planner.participant;

import com.anderson.planner.participant.exceptions.ParticipantNotFoundException;
import com.anderson.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository repository;

    public  void registerParticipantsToEvent(List<String> partricipantsToInvite, Trip trip
    ){
        List<Participant> participants = partricipantsToInvite.stream().map(email -> new Participant(email, trip)).toList();

        this.repository.saveAll(participants);

        System.out.println(participants.getFirst().getId());
    }

    public ParticipantCreateResponse registerParticipantToEvent(String email, Trip trip){
        Participant newParticipant = new Participant(email, trip);
        this.repository.save(newParticipant);
        return new ParticipantCreateResponse(newParticipant.getId());
    }

    public void triggerConfirmationEmailToParticipants(UUID id){}

    public void triggerConfirmationEmailToParticipant(String email){}

    public List<ParticipantData> getAllParticpantsFromEvent(UUID tripId) {
        List<Participant> participants = this.repository.findByTripId(tripId);
        if(participants.isEmpty()){
            throw new ParticipantNotFoundException("This trip has no participants");
        }
        return participants.stream()
                .map(participant -> new ParticipantData(participant.getId(),
                        participant.getName(),
                        participant.getEmail(),
                        participant.getIsConfirmed()))
                .collect(Collectors.toList());
    }
}
