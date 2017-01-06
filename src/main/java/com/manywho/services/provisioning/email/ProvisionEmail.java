package com.manywho.services.provisioning.email;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;
import java.util.UUID;

@Action.Metadata(name = "Provision Email", summary = "Provisions an Email for a given tenant", uri = "provision-email")
public class ProvisionEmail {

    public static class Input {
        @Action.Input(name = "Tenant ID", contentType = ContentType.String)
        private UUID tenant;

        public UUID getTenant() {
            return tenant;
        }
    }

    public static class Output {

        @Action.Output(name = "Host", contentType = ContentType.String)
        private String host;

        @Action.Output(name = "Transport", contentType = ContentType.String)
        private String transport;

        @Action.Output(name = "Port", contentType = ContentType.Number)
        private Integer port;

        @Action.Output(name = "Username", contentType = ContentType.String)
        private String username;

        @Action.Output(name = "Password", contentType = ContentType.String)
        private String password;

        public Output() {
        }

        public Output(String host, String transport, Integer port, String username, String password) {
            this.host = host;
            this.transport = transport;
            this.port = port;
            this.username = username;
            this.password = password;
        }

        public String getHost() {
            return host;
        }

        public String getTransport() {
            return transport;
        }

        public Integer getPort() {
            return port;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}