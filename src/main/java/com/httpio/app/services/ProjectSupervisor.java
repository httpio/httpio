package com.httpio.app.services;

import com.google.inject.Inject;
import com.httpio.app.models.Profile;
import com.httpio.app.models.Project;
import com.httpio.app.models.Request;
import com.httpio.app.services.Http.Methods;
import com.httpio.app.modules.Item;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The class responsible for reading and saving projects.
 */
public class ProjectSupervisor {
    private ObjectProperty<Project> project = new SimpleObjectProperty<>();
    private SimpleStringProperty projectFilePath = new SimpleStringProperty();

    private Http http;

    @Inject
    public void setHttp(Http http) {
        this.http = http;
    }

    public void loadJsonHolderProject() {
        Project project = new Project() {{
            setName("Jsonholder");
        }};

        // Profile
        project.addProfile(new Profile(){{
            setName("Development");
            setBaseURL("http://dev.jsonplaceholder.typicode.com");
        }});

        project.addProfile(new Profile(){{
            setName("Production");
            setBaseURL("http://jsonplaceholder.typicode.com");
        }});

        project.addProfile(new Profile(){{
            setName("Testing");
            setBaseURL("http://testing.jsonplaceholder.typicode.com");
        }});

        project.setProfile(project.getProfiles().get(0));

        // Request
        project.addRequest(new Request(){{
            setName("Todos");
            setMethod(http.getMethodById(Methods.GET));
            setUrl("/todos/1");
        }});

        project.addRequest(new Request(){{
            setName("Posts");
            setMethod(http.getMethodById(Methods.GET));
            setUrl("/posts");

            addRequest(new Request(){{
                setName("Post 1");
                setMethod(http.getMethodById(Methods.GET));
                setUrl("/1");

                addRequest(new Request(){{
                    setName("Comments for post 1");
                    setMethod(http.getMethodById(Methods.GET));
                    setUrl("/comments");
                }});
            }});
        }});

        // project.addRequest(new Request(){{
        //     setName("Comments");
        //     setMethod(http.getMethodById(Methods.GET));
        //     setUrl("/comments");

        //     addParameter(new Item(){{
        //         setName("postId");
        //         setValue("1");
        //     }});
        // }});

        // project.addRequest(new Request(){{
        //     setName("Post for user 1");
        //     setMethod(http.getMethodById(Methods.GET));
        //     setUrl("/posts");

        //     addParameter(new Item(){{
        //         setName("userId");
        //         setValue("1");
        //     }});
        // }});

        // project.addRequest(new Request(){{
        //     setName("Create post");
        //     setMethod(http.getMethodById(Methods.POST));
        //     setUrl("/posts");
        // }});

        // project.addRequest(new Request(){{
        //     setName("Update post");
        //     setMethod(http.getMethodById(Methods.PUT));
        //     setUrl("/posts");
        // }});

        // project.addRequest(new Request(){{
        //     setName("Patch post 1");
        //     setMethod(http.getMethodById(Methods.PATCH));
        //     setUrl("/posts/1");
        // }});

        // project.addRequest(new Request(){{
        //     setName("Delete post 1");
        //     setMethod(http.getMethodById(Methods.DELETE));
        //     setUrl("/posts/1");
        // }});

        project.setRequest(project.getRequests().get(0));

        this.project.setValue(project);
    }

    public Request createNewRequest() {
        Request request = new Request();

        request.setName("New request");
        request.setMethod(http.getMethodById(Methods.GET));
        request.setUrl("/...");

        return request;
    }

    public Profile createNewProfile() {
        Profile profile = new Profile();

        profile.setName("New profile");
        profile.setBaseURL("http://domain.com");

        return profile;
    }

    /**
     * Return active project.
     */
    public Project getProject() {
        return project.getValue();
    }

    /**
     * Set project and emits events of change.
     */
    public void setProject(Project project) {
        this.project.setValue(project);
    }

    public ObjectProperty<Project> projectProperty() {
        return project;
    }

    /**
     * Returns project file path.
     */
    public String getProjectFilePath() {
        return projectFilePath.get();
    }

    public SimpleStringProperty projectFilePathProperty() {
        return projectFilePath;
    }

    /**
     * Save active project.
     */
    public void save() throws Exception {
        save(project.getValue(), projectFilePath.getValue());
    }

    /**
     * Save active project.
     */
    public void save(String path) throws Exception {
        save(project.getValue(), path);
    }

    /**
     * Save project at given path.
     */
    public void save(Project project, String path) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.newDocument();

        // document.set
        Element elementProject = document.createElement("Project");

        elementProject.setAttribute("id", project.getId());
        elementProject.setAttribute("name", project.getName());
        elementProject.setAttribute("description", project.getDescription());

        if (project.getProfile() != null) {
            elementProject.setAttribute("profileId", project.getProfile().getId());
        }

        if (project.getRequest() != null) {
            elementProject.setAttribute("requestId", project.getRequest().getId());
        }

        document.appendChild(elementProject);

        // Profiles
        Element elementProfiles = document.createElement("Profiles");

        for(Profile profile: project.getProfiles()) {
            Element elementProfile = document.createElement("Profile");

            elementProfile.setAttribute("id", profile.getId());
            elementProfile.setAttribute("name", profile.getName());
            elementProfile.setAttribute("baseURL", profile.getBaseURL());
            elementProfile.setAttribute("description", profile.getDescription());

            // Variables
            Element elementVariables = document.createElement("Variables");

            for(Item variable: profile.getVariables()) {
                Element elementVariable = document.createElement("Variable");

                elementVariable.setAttribute("id", variable.getId());
                elementVariable.setAttribute("name", variable.getName());
                elementVariable.setAttribute("value", variable.getValue());

                elementVariables.appendChild(elementVariable);
            }

            elementProfile.appendChild(elementVariables);

            // Headers
            Element elementHeaders = document.createElement("Headers");

            for(Item header: profile.getHeaders()) {
                Element elementHeader = document.createElement("Header");

                elementHeader.setAttribute("id", header.getId());
                elementHeader.setAttribute("name", header.getName());
                elementHeader.setAttribute("value", header.getValue());

                elementHeaders.appendChild(elementHeader);
            }

            elementProfile.appendChild(elementHeaders);

            // Parameters
            Element elementParameters = document.createElement("Parameters");

            for(Item parameter: profile.getParameters()) {
                Element elementParameter = document.createElement("Parameter");

                elementParameter.setAttribute("id", parameter.getId());
                elementParameter.setAttribute("name", parameter.getName());
                elementParameter.setAttribute("value", parameter.getValue());

                elementParameters.appendChild(elementParameter);
            }

            elementProfile.appendChild(elementParameters);

            // Add element to profile
            elementProfiles.appendChild(elementProfile);
        }

        elementProject.appendChild(elementProfiles);

        // Requests
        Element elementRequests = document.createElement("Requests");

        saveRequest(elementRequests, project.getRequests(), document);

        elementProject.appendChild(elementRequests);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "PUBLIC");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(document), new StreamResult(new FileWriter(path)));

        // Set path after save
        projectFilePath.setValue(path);
    }

    private void saveRequest(Element elementParent, Collection<Request> requests, Document document) {

        for(Request request: requests) {
            Element elementRequest = document.createElement("Request");

            elementRequest.setAttribute("id", request.getId());
            elementRequest.setAttribute("name", request.getName());
            elementRequest.setAttribute("url", request.getUrl());
            elementRequest.setAttribute("body", request.getBody());
            elementRequest.setAttribute("standalone", request.isStandalone() ? "1" : "0");

            if (request.getMethod() != null) {
                elementRequest.setAttribute("method", request.getMethod().getId().toString());
            } else {
                elementRequest.setAttribute("method", null);
            }

            // Headers
            Element elementHeaders = document.createElement("Headers");

            for(Item item: request.getHeaders()) {
                Element element = document.createElement("Header");

                element.setAttribute("id", item.getId());
                element.setAttribute("name", item.getName());
                element.setAttribute("value", item.getValue());

                elementHeaders.appendChild(element);
            }

            elementRequest.appendChild(elementHeaders);

            // Parameters
            Element elementParameters = document.createElement("Parameters");

            for(Item item: request.getParameters()) {
                Element element = document.createElement("Parameter");

                element.setAttribute("id", item.getId());
                element.setAttribute("name", item.getName());
                element.setAttribute("value", item.getValue());

                elementParameters.appendChild(element);
            }

            elementRequest.appendChild(elementParameters);

            // Childs
            if (request.getRequests().size() > 0) {
                Element elementChilds = document.createElement("Childs");

                saveRequest(elementChilds, request.getRequests(), document);

                elementRequest.appendChild(elementChilds);
            }

            elementParent.appendChild(elementRequest);
        }
    }

    /**
     * Load project from given path.
     */
    public void load(String path) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(new File(path));
        Element elementProject = document.getDocumentElement();

        HashMap<String, Object> register = new HashMap<>();

        // Project
        Project project = new Project();

        project.setId(emptyNull(elementProject.getAttribute("id")));
        project.setName(emptyNull(elementProject.getAttribute("name")));
        project.setDescription(emptyNull(elementProject.getAttribute("description")));

        register.put(project.getId(), project);

        // Profile
        for(Element elementProfile: new NodeListIterator<Element>(elementProject.getElementsByTagName("Profile"))) {
            Profile profile = new Profile();

            profile.setId(emptyNull(elementProfile.getAttribute("id")));
            profile.setName(emptyNull(elementProfile.getAttribute("name")));
            profile.setBaseURL(emptyNull(elementProfile.getAttribute("baseURL")));
            profile.setDescription(emptyNull(elementProfile.getAttribute("description")));

            // Headers
            Node headers = findChildNodeByTagName(elementProfile, "Headers");

            if (headers != null) {
                for(Element elementItem: new NodeListIterator<Element>(headers.getChildNodes())) {
                    Item item = new Item();

                    item.setId(emptyNull(elementItem.getAttribute("id")));
                    item.setName(emptyNull(elementItem.getAttribute("name")));
                    item.setValue(emptyNull(elementItem.getAttribute("value")));

                    profile.addHeader(item);
                }
            }

            // Variables
            Node variables = findChildNodeByTagName(elementProfile, "Variables");

            if (variables != null) {
                for(Element elementItem: new NodeListIterator<Element>(variables.getChildNodes())) {
                    Item item = new Item();

                    item.setId(emptyNull(elementItem.getAttribute("id")));
                    item.setName(emptyNull(elementItem.getAttribute("name")));
                    item.setValue(emptyNull(elementItem.getAttribute("value")));

                    profile.addVariable(item);
                }
            }

            // Parameters
            Node parameters = findChildNodeByTagName(elementProfile, "Parameters");

            if (parameters != null) {
                for(Element elementItem: new NodeListIterator<Element>(parameters.getChildNodes())) {
                    Item item = new Item();

                    item.setId(emptyNull(elementItem.getAttribute("id")));
                    item.setName(emptyNull(elementItem.getAttribute("name")));
                    item.setValue(emptyNull(elementItem.getAttribute("value")));

                    profile.addParameter(item);
                }
            }

            // Add profile to register
            register.put(profile.getId(), profile);

            // Add profile to project
            project.addProfile(profile);
        }

        // Requests
        loadRequestsTo(project, findChildNodeByTagName(elementProject, "Requests"), register);

        // Set active profile and request
        String profileId = emptyNull(elementProject.getAttribute("profileId"));

        if (register.containsKey(profileId)) {
            project.setProfile((Profile) register.get(profileId));
        } else {
            // Something goes wrong. I select first profile.
            project.setProfile(project.getProfiles().get(0));
        }

        String requestId = emptyNull(elementProject.getAttribute("requestId"));

        if (register.containsKey(requestId)) {
            project.setRequest((Request) register.get(requestId));
        } else {
            project.setRequest(project.getRequests().get(0));
        }

        // Set project file path
        projectFilePath.setValue(path);

        this.project.setValue(project);

        // return project;
    }

    public void loadNewProject() {
         Project project = new Project() {{
            setName("New project");
        }};

        // Profile
        project.addProfile(new Profile(){{
            setName("Development");
            setBaseURL("http://domain.com");
        }});

        project.setProfile(project.getProfiles().get(0));

        // Request
        project.addRequest(new Request(){{
            setName("Users");
            setMethod(http.getMethodById(Methods.GET));
            setUrl("/users");
        }});

        project.setRequest(project.getRequests().get(0));

        this.project.setValue(project);
    }

    private String emptyNull(String value) {

        if (value == null) {
            return value;
        }

        return value.equals("") ? null : value;
    }

    private void loadRequestsTo(Object owner, Node nodeParent, HashMap<String, Object> register) {
        for(Element elementRequest: new NodeListIterator<Element>(nodeParent.getChildNodes())) {
            Request request = new Request();

            request.setId(emptyNull(elementRequest.getAttribute("id")));
            request.setUrl(emptyNull(elementRequest.getAttribute("url")));
            request.setMethod(http.getMethodById(emptyNull(elementRequest.getAttribute("method"))));
            request.setName(emptyNull(elementRequest.getAttribute("name")));
            request.setBody(emptyNull(elementRequest.getAttribute("body")));
            request.setStandalone(emptyNull(elementRequest.getAttribute("standalone")).equals("1"));

            // Headers
            Node headers = findChildNodeByTagName(elementRequest, "Headers");

            if (headers != null) {
                for(Element elementItem: new NodeListIterator<Element>(headers.getChildNodes())) {
                    Item item = new Item();

                    item.setId(emptyNull(elementItem.getAttribute("id")));
                    item.setName(emptyNull(elementItem.getAttribute("name")));
                    item.setValue(emptyNull(elementItem.getAttribute("value")));

                    request.addHeader(item);
                }
            }

            // Parameters
            Node parameters = findChildNodeByTagName(elementRequest, "Parameters");

            if (parameters != null) {
                for(Element elementItem: new NodeListIterator<Element>(parameters.getChildNodes())) {
                    Item item = new Item();

                    item.setId(emptyNull(elementItem.getAttribute("id")));
                    item.setName(emptyNull(elementItem.getAttribute("name")));
                    item.setValue(emptyNull(elementItem.getAttribute("value")));

                    request.addParameter(item);
                }
            }

            // Put co owner of request. Owner can be project or other request.
            if (owner instanceof Project) {
                ((Project) owner).addRequest(request);
            } else if (owner instanceof Request) {
                ((Request) owner).addRequest(request);
            } else {
                throw new ClassCastException("Incorrect call");
            }

            // Save at object register
            register.put(request.getId(), request);

            // Check if there are childs of request.
            Node nodeRequestChilds = findChildNodeByTagName(elementRequest, "Childs");

            if (nodeRequestChilds != null) {
                loadRequestsTo(request, nodeRequestChilds, register);
            }
        }
    }

    private Node findChildNodeByTagName(Node parent, String tag) {
        for(Node node: new NodeListIterator<Node>(parent.getChildNodes())) {
            if (node.getNodeName().equals(tag)) {
                return node;
            }
        }

        return null;
    }

    private static class NodeListIterator<T> implements Iterator<T>, Iterable<T>{
        int i = 0;

        Vector<Node> filtered = new Vector<>();

        NodeListIterator(NodeList nodes) {
            int length = nodes.getLength();

            for(int i=0; i < length; i++) {
                Node node = nodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    filtered.add(node);
                }
            }
        }

        @Override
        public boolean hasNext() {
            return i < filtered.size();
        }

        @Override
        public T next() {
            i++;

            return (T) filtered.get(i-1);
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }
}
