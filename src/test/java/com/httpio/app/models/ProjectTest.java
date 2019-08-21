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

        assertEquals("A15E89ED220036C61C857372B021D656", project.getChecksum());
    }

    private Project createProject() {
        Http http = new Http();

        Project project = new Project("A1") {{
            setName("Jsonholder");
        }};

        // Profile
        project.addProfile(new Profile("P1"){{
            setName("Development");
            setBaseURL("http://development.api.com");

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
            setBaseURL("http://prodution.api.com");
        }});

        project.addProfile(new Profile("P3"){{
            setName("Testing");
            setBaseURL("http://testing.api.com");
        }});

        // Set active profile
        project.setProfile(project.getProfiles().get(0));

        // Request
        Request request = new Request("R1") {{
            setName("Todos");
            setMethod(http.getMethodById(Methods.GET));
            setUrl("/todos/1");
        }};

        project.addRequest(new Request("R2"){{
            setName("Posts");
            setMethod(http.getMethodById(Methods.GET));
            setUrl("/posts");

            addRequest(new Request("R2R1"){{
                setName("Post 1");
                setMethod(http.getMethodById(Methods.GET));
                setUrl("/1");

                addRequest(new Request("R2R1R1"){{
                    setName("Comments for post 1");
                    setMethod(http.getMethodById(Methods.GET));
                    setUrl("/comments");
                }});
            }});
        }});

        project.addRequest(new Request("R3"){{
            setName("Comments");
            setMethod(http.getMethodById(Methods.GET));
            setUrl("/comments");

            addParameter(new Item("R3P1"){{
                setName("postId");
                setValue("1");
            }});
        }});

        project.addRequest(new Request("R4"){{
            setName("Post for user 1");
            setMethod(http.getMethodById(Methods.GET));
            setUrl("/posts");

            addParameter(new Item("R4P1"){{
                setName("userId");
                setValue("1");
            }});
        }});

        project.addRequest(new Request("R5"){{
            setName("Create post");
            setMethod(http.getMethodById(Methods.POST));
            setUrl("/posts");
        }});

        project.addRequest(new Request("R6"){{
            setName("Update post");
            setMethod(http.getMethodById(Methods.PUT));
            setUrl("/posts");
        }});

        project.addRequest(new Request("R7"){{
            setName("Patch post 1");
            setMethod(http.getMethodById(Methods.PATCH));
            setUrl("/posts/1");
        }});

        project.addRequest(new Request("R8"){{
            setName("Delete post 1");
            setMethod(http.getMethodById(Methods.DELETE));
            setUrl("/posts/1");
        }});

        project.setRequest(project.getRequests().get(0));

        return project;
    }
}