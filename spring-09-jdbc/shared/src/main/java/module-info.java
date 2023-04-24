module ru.otus.shared {

    requires lombok;
    requires spring.data.commons;
    requires spring.data.mongodb;
    requires jakarta.validation;

    exports ru.otus.shared.dto;
    exports ru.otus.shared.model;
    exports ru.otus.shared.validation;
}
