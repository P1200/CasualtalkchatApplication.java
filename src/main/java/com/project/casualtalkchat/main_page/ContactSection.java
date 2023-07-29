package com.project.casualtalkchat.main_page;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

@CssImport("./styles.css")
public class ContactSection extends Section {

    public ContactSection() {
        this.setClassName("container");
        this.setId("contact");

        Div content = new Div(getApplicationDescriptionPart(), getFormPart());
        content.setClassName("row d-flex justify-content-center");

        add(content);
    }

    private Component getFormPart() {

        NativeButton submitButton = new NativeButton("Send Message");
        Div button = new Div(submitButton);
        button.setClassName("text-center");

        Div nameAndEmail = new Div(getNameInput(), getEmailInput());
        nameAndEmail.setClassName("row");

        FormLayout form = new FormLayout(nameAndEmail, getSubjectInput(), getMessageInput(), button);
        form.setClassName("email-form");

        Div contactForm = new Div(form);
        contactForm.setClassName("col-lg-7 mt-5 mt-lg-0 d-flex align-items-stretch");
        return contactForm;
    }

    private Div getNameInput() {
        TextField nameInput = new TextField();
        nameInput.setId("name");
        NativeLabel nameLabel = new NativeLabel("Your name");
        nameLabel.setFor(nameInput);
        Div name = new Div(nameLabel, nameInput);
        name.setClassName("form-group col-md-6");
        return name;
    }

    private Div getEmailInput() {
        EmailField emailInput = new EmailField();
        emailInput.setId("email");
        NativeLabel emailLabel = new NativeLabel("Your email");
        emailLabel.setFor(emailInput);
        Div email = new Div(emailLabel, emailInput);
        email.setClassName("form-group col-md-6 mt-3 mt-md-0");
        return email;
    }

    private Div getSubjectInput() {
        TextField subjectInput = new TextField();
        subjectInput.setClassName("w-100");
        subjectInput.setId("subject");
        NativeLabel subjectLabel = new NativeLabel("Subject");
        subjectLabel.setFor(subjectInput);
        Div subject = new Div(subjectLabel, subjectInput);
        subject.setClassName("form-group mt-3");
        return subject;
    }

    private Div getMessageInput() {
        TextArea messageInput = new TextArea();
        messageInput.setRequired(true);
        messageInput.setClassName("w-100");
        messageInput.setId("message");
        NativeLabel messageLabel = new NativeLabel("Message");
        messageLabel.setFor(messageInput);
        Div message = new Div(messageLabel, messageInput);
        message.setClassName("form-group mt-3");
        return message;
    }

    private Component getApplicationDescriptionPart() {

        Div incentivePart = new Div(new Span("Contact"), new H2("Contact"),
                new Paragraph("Do you have any problem? Write to us"));
        incentivePart.setClassName("section-title w-100");

        return incentivePart;
    }
}
