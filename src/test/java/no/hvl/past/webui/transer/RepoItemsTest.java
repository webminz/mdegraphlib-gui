package no.hvl.past.webui.transer;

import no.hvl.past.webui.backend.service.UserServiceStub;
import no.hvl.past.webui.transfer.entities.File;
import no.hvl.past.webui.transfer.entities.Folder;
import no.hvl.past.webui.transfer.entities.User;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RepoItemsTest {

    static User SYSTEM_USER = new User(0L, "system", "system", null);
    static User PATRICK = new User(1L, "past@hvl.no", "Patrick Stünkel", null);


    @Test
    public void testNavigate() {
        Folder root = new Folder("root", null, SYSTEM_USER, LocalDateTime.MIN, LocalDateTime.MIN, false, new ArrayList<>());
        Folder system = new Folder("system", root, SYSTEM_USER, LocalDateTime.MIN, LocalDateTime.MIN, true, new ArrayList<>());
        Folder user = new Folder("past@hvl.no", root, PATRICK, LocalDateTime.of(2020, 8, 1, 11, 23), LocalDateTime.of(2020, 8, 1, 11, 23), false, new ArrayList<>());
        Folder projects = new Folder("Projects", user, PATRICK, LocalDateTime.of(2020, 8, 3, 7, 0), LocalDateTime.of(2020, 8, 3, 7, 0), false, new ArrayList<>());
        File f1 = new File("Ecore.emf", user, PATRICK, LocalDateTime.of(2020, 8, 1, 11, 23), LocalDateTime.of(2020, 8, 1, 11, 23), false, File.FileType.MODEL, "1.1");
        File f2 = new File("Test.g", user, PATRICK, LocalDateTime.of(2020, 8, 2, 10, 11), LocalDateTime.of(2020, 8, 2, 10, 11), false, File.FileType.MODEL, "1.2");
        File f3 = new File("Service.java", projects, PATRICK, LocalDateTime.of(2020, 8, 2, 10, 11), LocalDateTime.of(2020, 8, 2, 10, 11), false, File.FileType.CODE, "1.3");
        File f4 = new File("Requirements.docx", projects, PATRICK, LocalDateTime.of(2020, 8, 2, 10, 11), LocalDateTime.of(2020, 8, 2, 10, 11), true, File.FileType.BINARY, null);
        root.getChildren().add(system);
        root.getChildren().add(user);
        user.getChildren().add(f1);
        user.getChildren().add(f2);
        user.getChildren().add(projects);
        projects.getChildren().add(f3);
        projects.getChildren().add(f4);

        Assert.assertEquals(f2,root.navigate("root/past@hvl.no/Test.g"));
    }
}
