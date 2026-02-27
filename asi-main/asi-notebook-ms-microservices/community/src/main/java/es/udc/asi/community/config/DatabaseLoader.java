package es.udc.asi.community.config;

import es.udc.asi.community.model.Community;
import es.udc.asi.community.model.CommunityEntry;
import es.udc.asi.community.model.CommunityMember;
import es.udc.asi.community.repository.CommunityRepository;
import es.udc.asi.community.repository.CommunityMemberRepository;
import es.udc.asi.community.repository.CommunityEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseLoader implements CommandLineRunner {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository memberRepository;
    private final CommunityEntryRepository entryRepository;

    @Override
    public void run(String... args) {
        log.info("🌱 Inicializando datos de ejemplo para Comunidades...");

        // Limpiar datos existentes (solo para desarrollo)
        entryRepository.deleteAll();
        memberRepository.deleteAll();
        communityRepository.deleteAll();

        // Los IDs de usuario que existen en el sistema son:
        // 1 = pepemin (admin)
        // 2 = mariadmin (admin)
        // 3 = laura
        // 4 = pedroff
        // 5 = ramón

        // ========== COMUNIDAD 1: Runners Matutinos (creada por Laura - userId: 3) ==========
        Community runners = Community.builder()
                .name("Runners Matutinos")
                .creationReason("Motivarnos mutuamente para correr cada mañana")
                .description("Comunidad para personas que disfrutan correr temprano. Compartimos rutas, consejos y nos motivamos entre todos.")
                .creatorUserId("3") // Laura
                .build();
        runners = communityRepository.save(runners);
        log.info("✅ Comunidad creada: {}", runners.getName());

        // Laura es miembro ADMIN (creadora)
        CommunityMember lauraInRunners = CommunityMember.builder()
                .community(runners)
                .userId("3")
                .role(CommunityMember.MemberRole.ADMIN)
                .build();
        memberRepository.save(lauraInRunners);

        // Ramón se une como MEMBER (userId: 5)
        CommunityMember ramonInRunners = CommunityMember.builder()
                .community(runners)
                .userId("5")
                .role(CommunityMember.MemberRole.MEMBER)
                .build();
        memberRepository.save(ramonInRunners);

        // Publicación de Laura
        CommunityEntry lauraPost1 = CommunityEntry.builder()
                .community(runners)
                .authorUserId("3")
                .type(CommunityEntry.EntryType.MOTIVATION)
                .content("¡Bienvenidos a Runners Matutinos! Espero que esta comunidad nos ayude a mantener la constancia. ¿Alguien se anima a una ruta de 5k este sábado?")
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();
        entryRepository.save(lauraPost1);

        // Publicación de Ramón
        CommunityEntry ramonPost1 = CommunityEntry.builder()
                .community(runners)
                .authorUserId("5")
                .type(CommunityEntry.EntryType.REFLECTION)
                .content("¡Cuenta conmigo Laura! He estado corriendo 3 veces por semana y cada vez me siento mejor.")
                .createdAt(LocalDateTime.now().minusDays(4))
                .build();
        entryRepository.save(ramonPost1);

        // ========== COMUNIDAD 2: Vida Saludable (creada por Pedro - userId: 4) ==========
        Community vidaSaludable = Community.builder()
                .name("Vida Saludable")
                .creationReason("Compartir consejos sobre alimentación y bienestar")
                .description("Espacio para compartir recetas saludables, tips de nutrición y apoyarnos en nuestro camino hacia una vida más sana.")
                .creatorUserId("4") // Pedro
                .build();
        vidaSaludable = communityRepository.save(vidaSaludable);
        log.info("✅ Comunidad creada: {}", vidaSaludable.getName());

        // Pedro es miembro ADMIN (creador)
        CommunityMember pedroInVida = CommunityMember.builder()
                .community(vidaSaludable)
                .userId("4")
                .role(CommunityMember.MemberRole.ADMIN)
                .build();
        memberRepository.save(pedroInVida);

        // Laura también se une
        CommunityMember lauraInVida = CommunityMember.builder()
                .community(vidaSaludable)
                .userId("3")
                .role(CommunityMember.MemberRole.MEMBER)
                .build();
        memberRepository.save(lauraInVida);

        // Ramón también se une
        CommunityMember ramonInVida = CommunityMember.builder()
                .community(vidaSaludable)
                .userId("5")
                .role(CommunityMember.MemberRole.MEMBER)
                .build();
        memberRepository.save(ramonInVida);

        // Publicación de Pedro
        CommunityEntry pedroPost1 = CommunityEntry.builder()
                .community(vidaSaludable)
                .authorUserId("4")
                .type(CommunityEntry.EntryType.TIP)
                .content("¡Hola a todos! Hoy quiero compartir mi receta favorita de smoothie verde: espinacas, plátano, manzana y un poco de jengibre. ¡Está delicioso!")
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();
        entryRepository.save(pedroPost1);

        // ========== COMUNIDAD 3: Meditación y Mindfulness (creada por Laura - userId: 3) ==========
        Community meditacion = Community.builder()
                .name("Meditación y Mindfulness")
                .creationReason("Cultivar la paz interior y reducir el estrés")
                .description("Comunidad dedicada a la práctica de meditación y mindfulness. Compartimos técnicas, experiencias y nos apoyamos en nuestro viaje hacia la calma mental.")
                .creatorUserId("3") // Laura
                .build();
        meditacion = communityRepository.save(meditacion);
        log.info("✅ Comunidad creada: {}", meditacion.getName());

        // Laura es miembro ADMIN (creadora)
        CommunityMember lauraInMeditacion = CommunityMember.builder()
                .community(meditacion)
                .userId("3")
                .role(CommunityMember.MemberRole.ADMIN)
                .build();
        memberRepository.save(lauraInMeditacion);

        // Publicación de Laura
        CommunityEntry lauraPost2 = CommunityEntry.builder()
                .community(meditacion)
                .authorUserId("3")
                .type(CommunityEntry.EntryType.QUESTION)
                .content("¡Bienvenidos! Empecemos compartiendo: ¿Cuánto tiempo dedicas a meditar cada día? Yo estoy intentando hacer 10 minutos cada mañana.")
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();
        entryRepository.save(lauraPost2);

        // ========== COMUNIDAD 4: Productividad y Organización (creada por Ramón - userId: 5) ==========
        Community productividad = Community.builder()
                .name("Productividad y Organización")
                .creationReason("Mejorar nuestras habilidades de gestión del tiempo")
                .description("Para quienes buscan optimizar su tiempo, compartir herramientas de productividad y consejos de organización personal.")
                .creatorUserId("5") // Ramón
                .build();
        productividad = communityRepository.save(productividad);
        log.info("✅ Comunidad creada: {}", productividad.getName());

        // Ramón es miembro ADMIN (creador)
        CommunityMember ramonInProductividad = CommunityMember.builder()
                .community(productividad)
                .userId("5")
                .role(CommunityMember.MemberRole.ADMIN)
                .build();
        memberRepository.save(ramonInProductividad);

        // Pedro se une
        CommunityMember pedroInProductividad = CommunityMember.builder()
                .community(productividad)
                .userId("4")
                .role(CommunityMember.MemberRole.MEMBER)
                .build();
        memberRepository.save(pedroInProductividad);

        // Publicación de Ramón
        CommunityEntry ramonPost2 = CommunityEntry.builder()
                .community(productividad)
                .authorUserId("5")
                .type(CommunityEntry.EntryType.TIP)
                .content("He empezado a usar la técnica Pomodoro y me está ayudando mucho. ¿Alguien más la utiliza?")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        entryRepository.save(ramonPost2);

        // ========== COMUNIDAD 5: Finanzas Personales (creada por Pedro - userId: 4) ==========
        Community finanzas = Community.builder()
                .name("Finanzas Personales")
                .creationReason("Aprender a gestionar mejor nuestro dinero")
                .description("Comunidad para compartir consejos sobre ahorro, inversión y planificación financiera personal.")
                .creatorUserId("4") // Pedro
                .build();
        finanzas = communityRepository.save(finanzas);
        log.info("✅ Comunidad creada: {}", finanzas.getName());

        // Pedro es miembro ADMIN (creador)
        CommunityMember pedroInFinanzas = CommunityMember.builder()
                .community(finanzas)
                .userId("4")
                .role(CommunityMember.MemberRole.ADMIN)
                .build();
        memberRepository.save(pedroInFinanzas);

        log.info("✅ Datos de ejemplo cargados correctamente:");
        log.info("   - 5 comunidades creadas");
        log.info("   - Laura (userId: 3) creó 2 comunidades y está en 3 total");
        log.info("   - Ramón (userId: 5) creó 1 comunidad y está en 2 total");
        log.info("   - Pedro (userId: 4) creó 2 comunidades y está en 3 total");
        log.info("   - 5 publicaciones de ejemplo");
    }
}

