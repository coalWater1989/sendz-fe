package com.charbz.view;

import com.charbz.model.User;
import com.charbz.service.UserService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import org.springframework.http.ResponseEntity;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class UserView extends Dialog {

    private VerticalLayout verticalLayout;
    private HorizontalLayout horizontalButtonLayout;

    public UserView(String username, UserService userService) {
        verticalLayout = new VerticalLayout();
        horizontalButtonLayout = new HorizontalLayout();

        this.setClassName("alpha-layouts");
        this.setHeaderTitle("User profile: " + username);

        TextField firstNameTx = new TextField("First name");
        TextField lastNameTx = new TextField("Last name");

        ResponseEntity<User> reUser = userService.getUser(username);
        User user = reUser.getBody();

        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        firstNameTx.setValue(firstName != null ? firstName : "");
        lastNameTx.setValue(lastName != null ? lastName : "");

        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxHeight(200, Unit.PIXELS);
        upload.setMaxWidth(200, Unit.PIXELS);
        upload.setAcceptedFileTypes("image/jpeg", "image/jpg", "image/png");
        upload.setMaxFiles(1);

        ByteArrayOutputStream pngContent = new ByteArrayOutputStream();

        upload.addSucceededListener(event -> {
            String attachmentName = event.getFileName();
            try {
                BufferedImage inputImage = ImageIO.read(buffer.getInputStream(attachmentName));
                ImageIO.write(inputImage, "png", pngContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button saveBt = new Button("Save");
        saveBt.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        Button cancelBt = new Button("Cancel");
        cancelBt.addThemeVariants(ButtonVariant.LUMO_ERROR);

        horizontalButtonLayout.add(saveBt, cancelBt);

        verticalLayout.add(firstNameTx, lastNameTx, new Text("Upload user image"), upload, horizontalButtonLayout);

        saveBt.addClickListener(buttonClickEvent -> {
            user.setFirstName(firstNameTx.getValue());
            user.setLastName(lastNameTx.getValue());
            try {
                user.setImage(new SerialBlob(pngContent.toByteArray()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            userService.updateUserInfo(user);
            this.close();

            Notification notification = Notification.show("User profile updated!", 2000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        cancelBt.addClickListener(buttonClickEvent -> {
            this.close();
        });

        this.add(verticalLayout);
    }
}
