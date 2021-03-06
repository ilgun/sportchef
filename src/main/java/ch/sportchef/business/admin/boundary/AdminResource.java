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
package ch.sportchef.business.admin.boundary;

import ch.sportchef.business.configuration.control.ConfigurationService;
import ch.sportchef.business.configuration.entity.Configuration;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;

@Stateless
@Path("admin")
//@Produces({MediaType.TEXT_HTML})
public class AdminResource {

    @Inject
    private ConfigurationService configurationService;

    @GET
    public Response getAdminPage(@QueryParam("access-code") final String accessCode) throws IOException {
        final Configuration configuration = configurationService.getConfiguration();
        final String password = configuration.getAdminPassword();
        if (password == null || password.trim().isEmpty() || !password.trim().equals(accessCode.trim())) {
            return Response.status(FORBIDDEN).build();
        }

        final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("admin.html");
        final StringBuilder stringBuilder = new StringBuilder();

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return Response.ok(stringBuilder.toString()).build();
    }

}
