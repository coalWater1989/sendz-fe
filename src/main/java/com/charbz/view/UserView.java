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
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.FileData;
import org.springframework.http.ResponseEntity;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class UserView extends Dialog {

    private VerticalLayout verticalLayout;
    private HorizontalLayout horizontalButtonLayout;

    public UserView(String username, UserService userService) {
        verticalLayout = new VerticalLayout();
        horizontalButtonLayout = new HorizontalLayout();

        this.setHeaderTitle("User profile: " + username);

        TextField firstNameTx = new TextField("First name");
        TextField lastNameTx = new TextField("Last name");

        ResponseEntity<User> reUser = userService.getUser(username);
        User user = reUser.getBody();

        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        firstNameTx.setValue(firstName != null ? firstName : "");
        lastNameTx.setValue(lastName != null ? lastName : "");

        FileBuffer fileBuffer = new FileBuffer();
        Upload upload = new Upload(fileBuffer);
        upload.setMaxHeight(600, Unit.PIXELS);
        upload.setMaxWidth(600, Unit.PIXELS);

        upload.setAcceptedFileTypes("jpg", "png");
        upload.setMaxFiles(1);

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
                FileData fileData = fileBuffer.getFileData();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[40000];
                int count;
                while (true) {

                    if (!((count = fileBuffer.getInputStream().read(buffer)) != -1)) break;
                    output.write(buffer, 0, count);
                }

                if (fileData != null) {
                    user.setImage(new SerialBlob(output.toByteArray()));
                }
            } catch (IOException | SQLException e) {
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
