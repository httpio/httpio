package com.httpio.app.models;

import com.httpio.app.models.Profile;
import com.httpio.app.models.Project;
import com.httpio.app.models.Request;
import com.httpio.app.modules.Item;
import com.httpio.app.services.Http;
import com.httpio.app.services.Http.Methods;
import com.httpio.app.services.Http.Protocols;
import org.junit.Test;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

public class ProjectTest {
    @Test
    public void getChecksum() {
        Project project = createProject();

        String checksum = project.getChecksum();

        assertEquals("AA6B8AB827C5EAAC90B76187A5289552", project.getChecksum());
    }

    private Project createProject() {
        Http http = new Http();

        Project project = new Project("A1") {{
            setName("Json holder");
        }};

        // Profile
        project.addProfile(new Profile("P1"){{
            setName("Development");
            setHost("development.api.com");
            setProtocol(http.getProtocolById(Protocols.HTTPS));

            addHeader(new Item("P1H1"){{
                setName("SESSIONID");
                setValue("6a293d65-6825-4f44-8b9c-6a86dde65df2");
            }});

            addVariable(new Item("P1V1"){{
                setName("UserID");
                setValue("1000");
            }});

            addParameter(new Item("P1P1"){{
                setName("userId");
                setName("{UserID}");
            }});
        }});

        project.addProfile(new Profile("P2"){{
            setName("Production");
            setHost("prodution.api.com");
            setProtocol(http.getProtocolById(Protocols.HTTP));
        }});

        project.addProfile(new Profile("P3"){{
            setName("Testing");
            setHost("testing.api.com");
            setProtocol(http.getProtocolById(Protocols.HTTP));
        }});

        // Set active profile
        project.setProfile(project.getProfiles().get(0));

        // Request
        Request request = new Request("R1") {{
            setName("Todos");
            setMethod(http.getMethodById(Methods.GET));
            setResource("/todos/1");
        }};

        project.addRequest(new Request("R2"){{
            setName("Posts");
            setMethod(http.getMethodById(Methods.GET));
            setResource("/posts");

            addRequest(new Request("R2R1"){{
                setName("Post 1");
                setMethod(http.getMethodById(Methods.GET));
                setResource("/1");

                addRequest(new Request("R2R1R1"){{
                    setName("Comments for post 1");
                    setMethod(http.getMethodById(Methods.GET));
                    setResource("/comments");
                }});
            }});
        }});

        project.addRequest(new Request("R3"){{
            setName("Comments");
            setMethod(http.getMethodById(Methods.GET));
            setResource("/comments");

            addParameter(new Item("R3P1"){{
                setName("postId");
                setValue("1");
            }});
        }});

        project.addRequest(new Request("R4"){{
            setName("Post for user 1");
            setMethod(http.getMethodById(Methods.GET));
            setResource("/posts");

            addParameter(new Item("R4P1"){{
                setName("userId");
                setValue("1");
            }});
        }});

        project.addRequest(new Request("R5"){{
            setName("Create post");
            setMethod(http.getMethodById(Methods.POST));
            setResource("/posts");
        }});

        project.addRequest(new Request("R6"){{
            setName("Update post");
            setMethod(http.getMethodById(Methods.PUT));
            setResource("/posts");
        }});

        project.addRequest(new Request("R7"){{
            setName("Patch post 1");
            setMethod(http.getMethodById(Methods.PATCH));
            setResource("/posts/1");
        }});

        project.addRequest(new Request("R8"){{
            setName("Delete post 1");
            setMethod(http.getMethodById(Methods.DELETE));
            setResource("/posts/1");
        }});

        project.setRequest(project.getRequests().get(0));

        return project;
    }
}