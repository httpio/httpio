package com.httpio.app.services;

import com.httpio.app.models.Profile;
import com.httpio.app.models.Project;
import com.httpio.app.models.Request;
import com.httpio.app.modules.Item;
import com.httpio.app.services.Http.Methods;
import com.httpio.app.services.Http.Protocols;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class ProjectSupervisorTest {

    @Test
    public void createSimpleProject() {
        // ProjectSupervisor projectSupervisor = new ProjectSupervisor();

        // Project project = projectSupervisor.createSimpleProject();

        // Request r1 = project.getRequests().get(0);
        // Request r2 = project.getRequests().get(1);

        // Request r1r1 = r1.getRequests().get(0);

        // assertEquals(r1, r1r1.getParent());
        // assertEquals("get", r1r1.getMethod());
        // assertEquals("/offers/10", r1r1.getResourceFull());
        // assertEquals("Offer with id 10.", r1r1.getName());
    }

    @Test
    public void savingAndLoading() throws Exception {
        // Project supervisor
        ProjectSupervisor projectSupervisor = new ProjectSupervisor();
        projectSupervisor.setHttp(new Http());

        // Project
        Project project = createProject();

        // project.dump();

        // Save project
        projectSupervisor.save(project, "project.xml");

        // Load project
        projectSupervisor.load("project.xml");

        Project loaded = projectSupervisor.getProject();

        assertEquals(project.getChecksum(), loaded.getChecksum());
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
                setValue("{UserID}");
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
        project.addRequest(new Request("R1"){{
            setName("Todos");
            setMethod(http.getMethodById(Methods.GET));
            setResource("/todos/1");
        }});

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