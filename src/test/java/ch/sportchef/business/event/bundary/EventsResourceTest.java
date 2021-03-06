/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016 Marcus Fihlon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.business.event.bundary;

import ch.sportchef.business.event.boundary.EventResource;
import ch.sportchef.business.event.boundary.EventsResource;
import ch.sportchef.business.event.control.EventService;
import ch.sportchef.business.event.entity.Event;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleBuilders;
import org.needle4j.junit.NeedleRule;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventsResourceTest {

    @Rule
    public NeedleRule needleRule = NeedleBuilders.needleMockitoRule().build();

    @ObjectUnderTest
    private EventsResource eventsResource;

    @Inject
    private EventService eventServiceMock;

    @Inject
    private UriInfo uriInfoMock;

    @Inject
    private UriBuilder uriBuilderMock;

    @Test
    public void saveWithSuccess() throws URISyntaxException {
        // arrange
        final Event eventToCreate = Event.builder()
                .eventId(0L)
                .title("Testevent")
                .location("Testlocation")
                .date(LocalDate.of(2099, Month.DECEMBER, 31))
                .time(LocalTime.of(22, 0))
                .build();
        final Event savedEvent = eventToCreate.toBuilder()
                .eventId(1L)
                .build();
        final String location = "http://localhost:8080/sportchef/api/events/1";
        final URI uri = new URI(location);

        when(eventServiceMock.create(eventToCreate))
                .thenReturn(savedEvent);
        when(uriInfoMock.getAbsolutePathBuilder())
                .thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(anyString()))
                .thenReturn(uriBuilderMock);
        when(uriBuilderMock.build())
                .thenReturn(uri);

        // act
        final Response response = eventsResource.save(eventToCreate, uriInfoMock);

        //assert
        assertThat(response.getStatus(), is(CREATED.getStatusCode()));
        assertThat(response.getHeaderString("Location"), is(location));
        verify(eventServiceMock, times(1)).create(eventToCreate);
        verify(uriInfoMock, times(1)).getAbsolutePathBuilder();
        verify(uriBuilderMock, times(1)).path(anyString());
        verify(uriBuilderMock, times(1)).build();
    }

    @Test
    public void findAll() {
        // arrange
        final Event event1 = Event.builder()
                .eventId(1L)
                .title("Testevent")
                .location("Testlocation")
                .date(LocalDate.of(2099, Month.DECEMBER, 31))
                .time(LocalTime.of(22, 0))
                .build();
        final Event event2 = event1.toBuilder()
                .eventId(2L)
                .build();
        final List<Event> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        when(eventServiceMock.findAll())
                .thenReturn(events);

        // act
        final Response response = eventsResource.findAll();
        final List<Event> list = (List<Event>) response.getEntity();
        final Event responseEvent1 = list.get(0);
        final Event responseEvent2 = list.get(1);

        // assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(responseEvent1, is(event1));
        assertThat(responseEvent2, is(event2));
        verify(eventServiceMock, times(1)).findAll();
    }

    @Test
    public void find() {
        // arrange
        final long eventId = 1L;

        // act
        final EventResource eventResource = eventsResource.find(eventId);

        // assert
        assertThat(eventsResource, notNullValue());
    }
}
