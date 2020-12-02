package no.hvl.past.webui.frontend.users;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import no.hvl.past.webui.transfer.api.RepoService;
import no.hvl.past.webui.transfer.api.UserService;
import no.hvl.past.webui.transfer.entities.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Route("register")
public class RegistrationForm extends VerticalLayout {

    private UserService userService;
    private RepoService repoService;
    private FormLayout form;
    private TextField email;
    private TextField displayName;
    private PasswordField password;
    private PasswordField passwordConfirmation;
    private TextField avatarUrl;
    private Button registerBtn;

    Binder<RegistrationBean> binder = new BeanValidationBinder<>(RegistrationBean.class);

    public RegistrationForm(@Autowired UserService userService, @Autowired RepoService repoService) {
        this.userService = userService;
        this.repoService = repoService;
        addClassName("registration-form");

        add(new H1("Welcome!"));
        add(new Paragraph("Your are one form away from getting started as a new ModelVM user"));

        this.form = new FormLayout();
        add(form);

        email = new TextField("Your e-mail (= username)", "user@example.com");
        form.add(email);

        displayName = new TextField("Your name (optional)");
        form.add(displayName);

        password = new PasswordField("Choose password");
        form.add(password);

        passwordConfirmation = new PasswordField("Confirm password");
        passwordConfirmation.setRevealButtonVisible(false);
        form.add(passwordConfirmation);

        avatarUrl = new TextField("URL to your avatar (optional)");
        form.add(avatarUrl);

        registerBtn = new Button("Register");
        registerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerBtn.addClickListener(this::register);
        form.add(registerBtn);

        binder.setBean(new RegistrationBean());
        binder.bindInstanceFields(this);
        binder.addStatusChangeListener(evt -> registerBtn.setEnabled(isValid()));

    }

    private boolean isValid() {
        boolean pwCorrect = true;
        if (!password.getValue().equals(passwordConfirmation.getValue())) {
            pwCorrect = false;
            passwordConfirmation.setInvalid(true);
            passwordConfirmation.setErrorMessage("Passwords did not match");
        }
        return binder.isValid() && pwCorrect;
    }

    private void register(ClickEvent event) {
        if (isValid()) {
            User registered = userService.register(binder.getBean().getEmail(), binder.getBean().getPassword(), binder.getBean().getDisplayName(), binder.getBean().getAvatarUrl(), LocalDateTime.now());
            repoService.newUser(registered, LocalDateTime.now());
            UI.getCurrent().navigate("login");
        }
    }
}
