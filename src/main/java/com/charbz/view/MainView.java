package com.charbz.view;

import com.charbz.model.Send;
import com.charbz.service.SendzService;
import com.charbz.service.UserService;
import com.charbz.view.dialog.UserDialog;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.vaadin.stefan.table.Table;
import org.vaadin.stefan.table.TableRow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Route
public class MainView extends VerticalLayout {

    private static boolean loggedIn = false;
    private static String currentUser = "";

    private final SendzService sendzService;
    private final UserService userService;

    private HorizontalLayout topMenu = new HorizontalLayout();
    private HorizontalLayout topMenuLeft = new HorizontalLayout();
    private HorizontalLayout topMenuRight = new HorizontalLayout();
    private HorizontalLayout footerLayout = new HorizontalLayout();
    private VerticalLayout viewSendsLayout = new VerticalLayout();
    private VerticalLayout logSendsLayout = new VerticalLayout();
    private VerticalLayout viewSessionsLayout = new VerticalLayout();
    private VerticalLayout loginLayout = new VerticalLayout();
    private VerticalLayout signupLayout = new VerticalLayout();

    private ComboBox gymsCb = new ComboBox<>("Gym");
    private ComboBox gradesCb = new ComboBox<>("Grade");
    private ComboBox colorsCb = new ComboBox<>("Color");
    private ComboBox holdTypesCb = new ComboBox<>("Hold type");
    private ComboBox boulderTypecCb = new ComboBox<>("Boulder type");
    private ComboBox wallTypesCb = new ComboBox<>("Wall type");
    private ComboBox burnsCb = new ComboBox<>("# of burns");

    private List<String> listGyms = new ArrayList<>();
    private List<String> listGrades = new ArrayList<>();
    private List<String> listColors = new ArrayList<>();
    private List<String> listHoldTypes = new ArrayList<>();
    private List<String> listBoulderTypes = new ArrayList<>();
    private List<String> listWallTypes = new ArrayList<>();
    private List<String> listBurns = new ArrayList<>();

    private Button mySendsBt = new Button("My Sends");
    private Button mySessionsBt = new Button("My Sessions");
    private Button logSendsBt = new Button("Log Sends");
    private Button loginBt = new Button("Login");
    private Button logoutBt = new Button("Logout");
    private Button signupBt = new Button("Signup");

    DatePicker dateDp = new DatePicker("Date");

    public MainView(SendzService sendzService, UserService userService) {
        this.sendzService = sendzService;
        this.userService = userService;

        addClassName("mainview");
        setSizeFull();

        addButtonListeners();
        generateTopMenu();
        generateViewSendsLayout();
        generateLogSendsLayout();
        generateViewSessionsLayout();
        generateLoginLayout();
        generateSignupLayout();
        generateFooterLayout();
        setAlphaBackgroundLayouts();

        add(topMenu);
        add(footerLayout);
    }

    private void addButtonListeners() {
        mySendsBt.addClickListener(clickEvent -> {
            remove(logSendsLayout);
            remove(viewSessionsLayout);
            remove(viewSendsLayout);
            remove(signupLayout);
            remove(loginLayout);
            setButtonClicked(mySendsBt);
            viewSendsLayout = generateViewSendsLayout();
            add(viewSendsLayout);
        });

        mySessionsBt.addClickListener(clickEvent -> {
            remove(viewSendsLayout);
            remove(logSendsLayout);
            remove(signupLayout);
            remove(loginLayout);
            setButtonClicked(mySessionsBt);
            add(viewSessionsLayout);
        });

        logSendsBt.addClickListener(clickEvent -> {
            remove(viewSendsLayout);
            remove(viewSessionsLayout);
            remove(signupLayout);
            remove(loginLayout);
            setButtonClicked(logSendsBt);
            add(logSendsLayout);
            logSendsLayout = generateLogSendsLayout();
        });

        loginBt.addClickListener(clickEvent -> {
            remove(viewSendsLayout);
            remove(viewSessionsLayout);
            remove(logSendsLayout);
            remove(signupLayout);
            setButtonClicked(loginBt);
            add(loginLayout);
        });

        logoutBt.addClickListener(clickEvent -> {
            Dialog confirmLogoutDialog = new Dialog("Confirm logout?");
            confirmLogoutDialog.setClassName("alpha-layouts");
            Button okBt = new Button("Ok");
            okBt.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            Button cancelBt = new Button("Cancel");
            cancelBt.addThemeVariants(ButtonVariant.LUMO_ERROR);
            HorizontalLayout confirmLogoutLayout = new HorizontalLayout();
            confirmLogoutLayout.add(okBt);
            confirmLogoutLayout.add(cancelBt);
            confirmLogoutDialog.add(confirmLogoutLayout);
            confirmLogoutDialog.open();

            okBt.addClickListener(clickEvent2 -> {
                confirmLogoutDialog.close();
                loggedIn = false;
                currentUser = "";
                generateTopMenu();
                Notification notification = Notification.show("Logout successful!", 2000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                generateViewSendsLayout();
            });

            cancelBt.addClickListener(clickEvent2 -> {
                confirmLogoutDialog.close();
            });
        });

        signupBt.addClickListener(clickEvent -> {
            remove(viewSendsLayout);
            remove(viewSessionsLayout);
            remove(logSendsLayout);
            remove(loginLayout);
            add(signupLayout);
            setButtonClicked(signupBt);
        });

        mySendsBt.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        mySessionsBt.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        logSendsBt.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        loginBt.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        signupBt.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        logoutBt.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    }

    private void setButtonClicked(Button buttonClicked) {
        mySendsBt.setClassName("");
        mySessionsBt.setClassName("");
        logSendsBt.setClassName("");
        loginBt.setClassName("");
        logoutBt.setClassName("");
        signupBt.setClassName("");
        buttonClicked.setClassName("button-clicked");
    }

    private void generateTopMenu() {
        topMenu.removeAll();
        topMenuLeft.removeAll();
        topMenuRight.removeAll();

        topMenu.addClassName("topMenu");

        topMenu.setWidth("100%");
        topMenuLeft.add(mySendsBt, mySessionsBt, logSendsBt);

        if (loggedIn) {
            Button userBt = new Button(currentUser);
            userBt.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            userBt.addClickListener(clickEvent -> {
                UserDialog userDialog = new UserDialog(currentUser, userService);
                userDialog.open();
            });
            topMenuRight.add(userBt, logoutBt);
        } else {
            topMenuRight.add(loginBt, signupBt);
        }

        topMenuRight.setAlignItems(Alignment.END);
        topMenuRight.setJustifyContentMode(JustifyContentMode.END);

        topMenuLeft.setWidth("80%");
        topMenuRight.setWidth("20%");

        topMenu.setVerticalComponentAlignment(Alignment.CENTER);
        topMenuLeft.setVerticalComponentAlignment(Alignment.CENTER);
        topMenuRight.setVerticalComponentAlignment(Alignment.CENTER);

        topMenu.add(topMenuLeft, topMenuRight);
    }

    //TODO refactor
    //TODO no tab on show password

    private VerticalLayout generateViewSendsLayout() {
        viewSendsLayout.removeAll();

        if (loggedIn) {

            Table viewSendsTable = new Table();
            TableRow headerRow = viewSendsTable.addRow();

            headerRow.addHeaderCell().setText("Date");
            headerRow.addHeaderCell().setText("Gym");
            headerRow.addHeaderCell().setText("Grade");
            headerRow.addHeaderCell().setText("Color");
            headerRow.addHeaderCell().setText("Hold Type");
            headerRow.addHeaderCell().setText("Boulder Type");
            headerRow.addHeaderCell().setText("Wall Type");
            headerRow.addHeaderCell().setText("# of Burns");

            List<LinkedHashMap<String, String>> sends = sendzService.getSends(currentUser).getBody();

            if (sends.isEmpty()) {
                Text noSendsTxt = new Text("You have no sends logged.");
                viewSendsLayout.add(noSendsTxt);
            } else {
                sends.stream().forEach((send) -> {
                    TableRow tableRow = viewSendsTable.addRow();

                    String color = send.get("color");
                    String burn = send.get("burnNumber");
                    String boulderType = send.get("boulderType");
                    String grade = send.get("grade");
                    String gym = send.get("gym");
                    String holdType = send.get("holdType");
                    String wallType = send.get("wallType");
                    String sessionDate = send.get("sessionDate").toString();
                    tableRow.addCells(sessionDate, gym, grade, color, holdType, boulderType, wallType, burn);
                });

                viewSendsLayout.add(viewSendsTable);
            }
        } else {
            Text loginMessageTxt = new Text("You have to be logged in to view your sends.");
            viewSendsLayout.add(loginMessageTxt);
        }

        viewSendsLayout.setAlignItems(Alignment.CENTER);
        viewSendsLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        return viewSendsLayout;
    }

    private VerticalLayout generateLogSendsLayout() {
        logSendsLayout.removeAll();

        if (loggedIn) {
            HorizontalLayout hl1 = new HorizontalLayout();
            hl1.add(gymsCb, gradesCb, colorsCb, holdTypesCb);

            HorizontalLayout hl2 = new HorizontalLayout();
            hl2.add(boulderTypecCb, wallTypesCb, burnsCb, dateDp);

            HorizontalLayout hl3 = new HorizontalLayout();

            initComboboxes();

            Button logSendBt = new Button("Log Send");
            logSendBt.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            logSendBt.addClickShortcut(Key.ENTER);
            logSendBt.addClickListener(clickEvent -> {
                Send send = new Send();

                if (boulderTypecCb.getValue() != null && burnsCb.getValue() != null && colorsCb.getValue() != null && gradesCb.getValue() != null
                        && gymsCb.getValue() != null && holdTypesCb.getValue() != null && dateDp.getValue() != null && wallTypesCb.getValue() != null) {
                    if (dateDp.getValue().isAfter(LocalDate.now())) {
                        Notification notification = Notification.show("You can't log a send in the future!", 2000, Notification.Position.MIDDLE);
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    } else {
                        send.setBoulderType(boulderTypecCb.getValue().toString());
                        send.setBurnNumber(burnsCb.getValue().toString());
                        send.setColor(colorsCb.getValue().toString());
                        send.setGym(gymsCb.getValue().toString());
                        send.setHoldType(holdTypesCb.getValue().toString());
                        send.setSessionDate(dateDp.getValue().toString());
                        send.setWallType(wallTypesCb.getValue().toString());
                        send.setGrade(gradesCb.getValue().toString());
                        send.setUsername(currentUser);

                        sendzService.logSend(send);

                        gymsCb.clear();
                        gradesCb.clear();
                        colorsCb.clear();
                        holdTypesCb.clear();
                        boulderTypecCb.clear();
                        wallTypesCb.clear();
                        burnsCb.clear();
                        dateDp.clear();

                        Notification notification = Notification.show("Send logged!", 2000, Notification.Position.MIDDLE);
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    }
                } else {
                    Notification notification = Notification.show("Mandatory fields missing.", 2000, Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });

            Button clearBt = new Button("Clear");
            clearBt.addThemeVariants(ButtonVariant.LUMO_ERROR);

            clearBt.addClickListener(clickEvent -> {
                gymsCb.clear();
                gradesCb.clear();
                colorsCb.clear();
                holdTypesCb.clear();
                boulderTypecCb.clear();
                wallTypesCb.clear();
                burnsCb.clear();
                dateDp.clear();
            });

            hl3.add(logSendBt, clearBt);

            hl1.setAlignItems(Alignment.CENTER);
            hl1.setJustifyContentMode(JustifyContentMode.CENTER);

            hl2.setAlignItems(Alignment.CENTER);
            hl2.setJustifyContentMode(JustifyContentMode.CENTER);

            hl3.setAlignItems(Alignment.CENTER);
            hl3.setJustifyContentMode(JustifyContentMode.CENTER);

            logSendsLayout.add(hl1, hl2, hl3);
        } else {
            Text loginMessageTxt = new Text("You have to be logged in to log your sends.");
            logSendsLayout.add(loginMessageTxt);
        }

        logSendsLayout.setAlignItems(Alignment.CENTER);
        logSendsLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        return logSendsLayout;
    }

    private void initComboboxes() {
        gymsCb.addFocusListener(focusEvent -> {
            if (listGyms.isEmpty()) {
                gymsCb.setItems(sendzService.getGyms().getBody());
            }
        });

        gradesCb.addFocusListener(focusEvent -> {
            if (listGrades.isEmpty()) {
                gradesCb.setItems(sendzService.getGrades().getBody());
            }
        });

        colorsCb.addFocusListener(focusEvent -> {
            if (listColors.isEmpty()) {
                colorsCb.setItems(sendzService.getColors().getBody());
            }
        });

        holdTypesCb.addFocusListener(focusEvent -> {
            if (listHoldTypes.isEmpty()) {
                holdTypesCb.setItems(sendzService.getHoldTypes().getBody());
            }
        });

        boulderTypecCb.addFocusListener(focusEvent -> {
            if (listBoulderTypes.isEmpty()) {
                boulderTypecCb.setItems(sendzService.getBoulderTypes().getBody());
            }
        });

        wallTypesCb.addFocusListener(focusEvent -> {
            if (listWallTypes.isEmpty()) {
                wallTypesCb.setItems(sendzService.getWallTypes().getBody());
            }
        });

        burnsCb.addFocusListener(focusEvent -> {
            if (listBurns.isEmpty()) {
                burnsCb.setItems(sendzService.getBurns().getBody());
            }
        });

        DatePicker dateDp = new DatePicker("Date");

        gymsCb.setRequired(true);
        gradesCb.setRequired(true);
        colorsCb.setRequired(true);
        holdTypesCb.setRequired(true);
        boulderTypecCb.setRequired(true);
        wallTypesCb.setRequired(true);
        burnsCb.setRequired(true);
        dateDp.setRequired(true);
    }

    private void generateViewSessionsLayout() {
        Text text = new Text("View Sessions Layout");
        viewSessionsLayout.setAlignItems(Alignment.CENTER);
        viewSessionsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        viewSessionsLayout.add(text);
    }

    private void generateLoginLayout() {
        Text loginText = new Text("Login");
        loginLayout.add(loginText);

        TextField userTf = new TextField();
        userTf.setLabel("Username");
        userTf.setRequired(true);
        loginLayout.add(userTf);

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setRequired(true);
        passwordField.setRevealButtonVisible(true);
        loginLayout.add(passwordField);

        Button loginBt = new Button("Login");
        loginBt.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        loginBt.addClickShortcut(Key.ENTER);
        loginBt.addClickListener(clickEvent -> {
            Text text = new Text("");
            loginLayout.remove(text);

            if (userService.login(userTf.getValue(), passwordField.getValue()).getBody()) {
                Notification notification = Notification.show("Login successful!", 2000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                loggedIn = true;
                currentUser = userTf.getValue();
                generateTopMenu();
                generateViewSendsLayout();
                remove(loginLayout);
                add(viewSendsLayout);
            } else {
                Notification notification = Notification.show("Invalid username/password", 2000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

            loginLayout.add(text);

            userTf.clear();
            passwordField.clear();
        });

        loginLayout.setAlignItems(Alignment.CENTER);
        loginLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        loginLayout.add(loginText, userTf, passwordField, loginBt);
    }

    private void generateSignupLayout() {
        Text signupText = new Text("Signup");
        signupLayout.add(signupText);

        TextField userTf = new TextField();
        userTf.setLabel("Username");
        userTf.setRequired(true);
        signupLayout.add(userTf);

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setRequired(true);
        passwordField.setRevealButtonVisible(true);
        signupLayout.add(passwordField);

        PasswordField confirmPasswordField = new PasswordField("Confirm password");
        confirmPasswordField.setRequired(true);
        confirmPasswordField.setRevealButtonVisible(true);
        signupLayout.add(confirmPasswordField);

        Button signupBt = new Button("Signup");
        signupBt.addClickShortcut(Key.ENTER);
        signupBt.addClickListener(clickEvent -> {
            if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
                Notification notification = Notification.show("Passwords must match!", 2000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                passwordField.clear();
                confirmPasswordField.clear();
            } else if (userService.signup(userTf.getValue(), passwordField.getValue()).getBody()) {
                Notification notification = Notification.show("Signup success!", 2000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                signupLayout.removeAll();
                loggedIn = true;
                currentUser = userTf.getValue();
                generateTopMenu();
                remove(viewSendsLayout);
                generateViewSendsLayout();
                add(viewSendsLayout);
            } else {
                Notification notification = Notification.show("Signup error!", 2000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                userTf.clear();
                passwordField.clear();
                confirmPasswordField.clear();
            }
        });

        signupLayout.setAlignItems(Alignment.CENTER);
        signupLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        signupLayout.add(signupText, userTf, passwordField, confirmPasswordField, signupBt);
    }

    private void generateFooterLayout() {
        Text footerTxt = new Text("SENDZ APP by Charbz. © 2023");

        Button facebookButton = new Button("",
                new Icon(VaadinIcon.FACEBOOK_SQUARE));
        facebookButton.setIconAfterText(true);

        Button emailButton = new Button("",
                new Icon(VaadinIcon.ENVELOPE));
        emailButton.setIconAfterText(true);

        Button youtubeButton = new Button("",
                new Icon(VaadinIcon.YOUTUBE_SQUARE));
        youtubeButton.setIconAfterText(true);

        footerLayout.add(footerTxt);
        footerLayout.add(facebookButton);
        footerLayout.add(emailButton);
        footerLayout.add(youtubeButton);
        footerLayout.setWidth("100%");
        footerLayout.setAlignItems(Alignment.CENTER);
        footerLayout.setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private void setAlphaBackgroundLayouts() {
        viewSendsLayout.setClassName("alpha-layouts");
        logSendsLayout.setClassName("alpha-layouts");
        viewSessionsLayout.setClassName("alpha-layouts");
        loginLayout.setClassName("alpha-layouts");
        signupLayout.setClassName("alpha-layouts");
        footerLayout.setClassName("footer-layout");
    }
}
