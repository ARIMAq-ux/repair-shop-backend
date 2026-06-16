package com.example.repairshop.config;

import com.example.repairshop.entity.*;
import com.example.repairshop.enums.*;
import com.example.repairshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final ClientRepository clientRepo;
    private final DeviceRepository deviceRepo;
    private final TechnicianRepository techRepo;
    private final RepairRequestRepository requestRepo;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepo.count() > 0) { log.info("Данные уже есть"); return; }

        log.info("=== Инициализация 5 клиентов, 5 устройств, 5 техников, 5 заявок ===");

        Role adminRole = roleRepo.save(new Role("ROLE_ADMIN"));
        Role userRole = roleRepo.save(new Role("ROLE_USER"));

        userRepo.save(User.builder().username("admin").password(encoder.encode("password"))
                .email("admin@repair.local").active(true).roles(Set.of(adminRole)).build());
        userRepo.save(User.builder().username("user1").password(encoder.encode("password"))
                .email("user1@repair.local").active(true).roles(Set.of(userRole)).build());

        // 5 клиентов
        Client c1 = clientRepo.save(Client.builder().fullName("Иван Петров").phone("+79161111111").email("ivan@mail.com").address("Москва, ул. Ленина, 1").build());
        Client c2 = clientRepo.save(Client.builder().fullName("Мария Сидорова").phone("+79162222222").email("maria@mail.com").address("Москва, ул. Пушкина, 2").build());
        Client c3 = clientRepo.save(Client.builder().fullName("Алексей Иванов").phone("+79163333333").email("alex@mail.com").address("Москва, ул. Тверская, 3").build());
        Client c4 = clientRepo.save(Client.builder().fullName("Ольга Смирнова").phone("+79164444444").email("olga@mail.com").address("Москва, ул. Арбат, 4").build());
        Client c5 = clientRepo.save(Client.builder().fullName("Дмитрий Кузнецов").phone("+79165555555").email("dmitry@mail.com").address("Москва, ул. Садовая, 5").build());
        log.info("Создано 5 клиентов");

        // 5 устройств
        Device d1 = deviceRepo.save(Device.builder().client(c1).deviceType(DeviceType.SMARTPHONE).brand("Samsung").model("Galaxy S23").serialNumber("SN-001").build());
        Device d2 = deviceRepo.save(Device.builder().client(c2).deviceType(DeviceType.LAPTOP).brand("Apple").model("MacBook Pro 14").serialNumber("SN-002").build());
        Device d3 = deviceRepo.save(Device.builder().client(c3).deviceType(DeviceType.TV).brand("LG").model("OLED55C2").serialNumber("SN-003").build());
        Device d4 = deviceRepo.save(Device.builder().client(c4).deviceType(DeviceType.TABLET).brand("Apple").model("iPad Air").serialNumber("SN-004").build());
        Device d5 = deviceRepo.save(Device.builder().client(c5).deviceType(DeviceType.PC).brand("Asus").model("ROG Strix").serialNumber("SN-005").build());
        log.info("Создано 5 устройств");

        // 5 техников
        Technician t1 = techRepo.save(Technician.builder().fullName("Алексей Мастеров").phone("+79051111111").email("alex@repair.local").specialization("Смартфоны").active(true).build());
        Technician t2 = techRepo.save(Technician.builder().fullName("Дмитрий Ноутбуков").phone("+79052222222").email("dmitry@repair.local").specialization("Ноутбуки").active(true).build());
        Technician t3 = techRepo.save(Technician.builder().fullName("Сергей Телевизоров").phone("+79053333333").email("sergey@repair.local").specialization("Телевизоры").active(true).build());
        Technician t4 = techRepo.save(Technician.builder().fullName("Андрей Планшетов").phone("+79054444444").email("andrey@repair.local").specialization("Планшеты").active(true).build());
        Technician t5 = techRepo.save(Technician.builder().fullName("Виктор Компьютеров").phone("+79055555555").email("viktor@repair.local").specialization("Компьютеры").active(true).build());
        log.info("Создано 5 техников");

        // 5 заявок
        requestRepo.save(RepairRequest.builder().client(c1).device(d1).technician(t1).problemDescription("Разбит экран, требуется замена").status(RequestStatus.DIAGNOSTICS).build());
        requestRepo.save(RepairRequest.builder().client(c2).device(d2).technician(t2).problemDescription("Не включается после залития").status(RequestStatus.NEW).build());
        requestRepo.save(RepairRequest.builder().client(c3).device(d3).technician(t3).problemDescription("Нет изображения, звук есть").status(RequestStatus.IN_PROGRESS).build());
        requestRepo.save(RepairRequest.builder().client(c4).device(d4).technician(t4).problemDescription("Не заряжается").status(RequestStatus.WAITING_PARTS).build());
        requestRepo.save(RepairRequest.builder().client(c5).device(d5).technician(t5).problemDescription("Синий экран смерти").status(RequestStatus.READY).build());
        log.info("Создано 5 заявок");

        log.info("=== Инициализация завершена ===");
    }
}