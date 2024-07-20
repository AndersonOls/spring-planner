package com.anderson.planner.link;

import com.anderson.planner.link.exceptions.LinkNotFoundException;
import com.anderson.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LinkService {

    @Autowired
    private LinkRepository repository;


    public LinkResponse registerLink(LinkRequestPayload payload, Trip trip){
        Link newLink = new Link(payload.title(), payload.url(), trip);
        this.repository.save(newLink);
        return new LinkResponse(newLink.getId());
    }

    public List<LinkData> getAllLinksFromTrip(UUID tripId){
        List<Link> links = this.repository.findByTripId(tripId);
        if(links.isEmpty()){
            throw new LinkNotFoundException("This trip has no links");
        }
        return links.stream().map(link -> new LinkData(link.getId(),
                link.getTitle(),
                link.getUrl()))
                .collect(Collectors.toList());
    }
}
