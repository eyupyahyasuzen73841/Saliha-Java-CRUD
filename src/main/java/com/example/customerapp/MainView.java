package com.example.customerapp;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {

    private final CustomerRepository repository;

    private final Grid<Customer> grid = new Grid<>(Customer.class);
    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");

    private Customer selectedCustomer;

    public MainView(CustomerRepository repository) {
        this.repository = repository;

        grid.setColumns("id", "firstName", "lastName");

        grid.asSingleSelect().addValueChangeListener(event -> {
            selectedCustomer = event.getValue();

            if (selectedCustomer != null) {
                firstName.setValue(selectedCustomer.getFirstName());
                lastName.setValue(selectedCustomer.getLastName());
            } else {
                firstName.clear();
                lastName.clear();
            }
        });

        Button newButton = new Button("New customer", event -> clearForm());
        Button saveButton = new Button("Save", event -> saveCustomer());
        Button deleteButton = new Button("Delete", event -> deleteCustomer());

        add(
                new H2("Customer Management"),
                grid,
                new HorizontalLayout(firstName, lastName),
                new HorizontalLayout(newButton, saveButton, deleteButton)
        );

        refreshGrid();
    }

    private void saveCustomer() {
        if (firstName.getValue().isBlank() || lastName.getValue().isBlank()) {
            Notification.show("Please enter first name and last name.");
            return;
        }

        Customer customer = selectedCustomer;

        if (customer == null) {
            customer = new Customer();
        }

        customer.setFirstName(firstName.getValue().trim());
        customer.setLastName(lastName.getValue().trim());

        repository.save(customer);
        clearForm();
        refreshGrid();

        Notification.show("Customer saved.");
    }

    private void deleteCustomer() {
        if (selectedCustomer == null) {
            Notification.show("Please select a customer.");
            return;
        }

        repository.delete(selectedCustomer);
        clearForm();
        refreshGrid();

        Notification.show("Customer deleted.");
    }

    private void clearForm() {
        grid.deselectAll();
        selectedCustomer = null;
        firstName.clear();
        lastName.clear();
    }

    private void refreshGrid() {
        grid.setItems(repository.findAll());
    }
}