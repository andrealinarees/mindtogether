package com.mindtogether.community.config;

import com.mindtogether.community.model.Community;
import com.mindtogether.community.model.CommunityEntry;
import com.mindtogether.community.model.CommunityMember;
import com.mindtogether.community.repository.CommunityRepository;
import com.mindtogether.community.repository.CommunityMemberRepository;
import com.mindtogether.community.repository.CommunityEntryRepository;
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
        log.info("🌱 Inicializando datos de ejemplo para Círculos de Apoyo...");

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

        // ========== CÍRCULO 1: Manejo de la Ansiedad (creado por Laura - userId: 3) ==========
        Community ansiedad = Community.builder()
                .name("Manejo de la Ansiedad")
                .creationReason("Apoyarnos mutuamente para aprender a gestionar la ansiedad en el día a día")
                .description("Espacio seguro para personas que experimentan ansiedad. Compartimos técnicas de respiración, estrategias de afrontamiento y nos acompañamos en los momentos difíciles. Aquí nadie juzga, todos aprendemos juntos.")
                .creatorUserId("3") // Laura
                .build();
        ansiedad = communityRepository.save(ansiedad);
        log.info("✅ Círculo creado: {}", ansiedad.getName());

        // Laura es miembro ADMIN (creadora)
        memberRepository.save(CommunityMember.builder()
                .community(ansiedad)
                .userId("3")
                .username("laura")
                .anonymous(false)
                .role(CommunityMember.MemberRole.ADMIN)
                .build());

        // Ramón se une (userId: 5)
        memberRepository.save(CommunityMember.builder()
                .community(ansiedad)
                .userId("5")
                .username("ramón")
                .anonymous(false)
                .role(CommunityMember.MemberRole.MEMBER)
                .build());

        // Pedro se une (userId: 4)
        memberRepository.save(CommunityMember.builder()
                .community(ansiedad)
                .userId("4")
                .username("pedroff")
                .anonymous(false)
                .role(CommunityMember.MemberRole.MEMBER)
                .build());

        // Publicaciones
        entryRepository.save(CommunityEntry.builder()
                .community(ansiedad).authorUserId("3").type(CommunityEntry.EntryType.MOTIVATION)
                .content("¡Bienvenidos al círculo! 💙 Recuerden que sentir ansiedad no nos define. Estamos aquí para apoyarnos. Compartan lo que les funciona para calmarse cuando sienten que la ansiedad aparece.")
                .createdAt(LocalDateTime.now().minusDays(5)).build());

        entryRepository.save(CommunityEntry.builder()
                .community(ansiedad).authorUserId("5").type(CommunityEntry.EntryType.TIP)
                .content("A mí me funciona mucho la técnica 5-4-3-2-1: nombrar 5 cosas que veo, 4 que toco, 3 que escucho, 2 que huelo y 1 que saboreo. Me ayuda a volver al presente cuando la ansiedad me invade.")
                .createdAt(LocalDateTime.now().minusDays(4)).build());

        entryRepository.save(CommunityEntry.builder()
                .community(ansiedad).authorUserId("4").type(CommunityEntry.EntryType.REFLECTION)
                .content("Hoy tuve un episodio de ansiedad en el trabajo pero logré controlarlo con respiración diafragmática. Hace un mes no hubiera podido. Pequeños avances 💪")
                .createdAt(LocalDateTime.now().minusDays(3)).build());

        // ========== CÍRCULO 2: Superando la Depresión Juntos (creado por Pedro - userId: 4) ==========
        Community depresion = Community.builder()
                .name("Superando la Depresión Juntos")
                .creationReason("Crear un espacio de acompañamiento para quienes luchan contra la depresión")
                .description("Círculo de apoyo para personas que atraviesan depresión o la han superado. Compartimos experiencias, logros por pequeños que sean, y nos recordamos que no estamos solos. La recuperación es posible.")
                .creatorUserId("4") // Pedro
                .build();
        depresion = communityRepository.save(depresion);
        log.info("✅ Círculo creado: {}", depresion.getName());

        memberRepository.save(CommunityMember.builder()
                .community(depresion)
                .userId("4")
                .username("pedroff")
                .anonymous(false)
                .role(CommunityMember.MemberRole.ADMIN)
                .build());

        memberRepository.save(CommunityMember.builder()
                .community(depresion)
                .userId("3")
                .username("laura")
                .anonymous(false)
                .role(CommunityMember.MemberRole.MEMBER)
                .build());

        entryRepository.save(CommunityEntry.builder()
                .community(depresion).authorUserId("4").type(CommunityEntry.EntryType.MOTIVATION)
                .content("Hoy logré levantarme temprano, ducharme y desayunar bien. Parece poco, pero para mí es un gran paso. No subestimen los pequeños logros. 🌅")
                .createdAt(LocalDateTime.now().minusDays(3)).build());

        entryRepository.save(CommunityEntry.builder()
                .community(depresion).authorUserId("3").type(CommunityEntry.EntryType.TIP)
                .content("Mi terapeuta me recomendó escribir cada noche 3 cosas buenas que pasaron en el día, por pequeñas que sean. Al principio costaba, pero ahora me ayuda a ver que no todo es gris.")
                .createdAt(LocalDateTime.now().minusDays(2)).build());

        // ========== CÍRCULO 3: Mindfulness y Meditación (creado por Laura - userId: 3) ==========
        Community mindfulness = Community.builder()
                .name("Mindfulness y Meditación")
                .creationReason("Cultivar la calma interior y reducir el estrés a través de prácticas de atención plena")
                .description("Círculo dedicado a la meditación, mindfulness y técnicas de relajación. Compartimos guías, experiencias y nos motivamos para mantener una práctica constante. Ideal para principiantes y practicantes.")
                .creatorUserId("3") // Laura
                .build();
        mindfulness = communityRepository.save(mindfulness);
        log.info("✅ Círculo creado: {}", mindfulness.getName());

        memberRepository.save(CommunityMember.builder()
                .community(mindfulness)
                .userId("3")
                .username("laura")
                .anonymous(false)
                .role(CommunityMember.MemberRole.ADMIN)
                .build());

        memberRepository.save(CommunityMember.builder()
                .community(mindfulness)
                .userId("5")
                .username("ramón")
                .anonymous(false)
                .role(CommunityMember.MemberRole.MEMBER)
                .build());

        entryRepository.save(CommunityEntry.builder()
                .community(mindfulness).authorUserId("3").type(CommunityEntry.EntryType.QUESTION)
                .content("¡Bienvenidos! 🧘 ¿Cuánto tiempo dedican a meditar cada día? Yo estoy intentando hacer 10 minutos cada mañana y noto una gran diferencia en mi nivel de estrés.")
                .createdAt(LocalDateTime.now().minusDays(2)).build());

        entryRepository.save(CommunityEntry.builder()
                .community(mindfulness).authorUserId("5").type(CommunityEntry.EntryType.TIP)
                .content("Para los que están empezando: prueben la meditación de escaneo corporal antes de dormir. Se trata de recorrer mentalmente cada parte del cuerpo relajándola. Duermo mucho mejor desde que lo hago. 🌙")
                .createdAt(LocalDateTime.now().minusDays(1)).build());

        // ========== CÍRCULO 4: Gestión del Estrés Laboral (creado por Ramón - userId: 5) ==========
        Community estresLaboral = Community.builder()
                .name("Gestión del Estrés Laboral")
                .creationReason("Compartir estrategias para manejar el estrés y prevenir el burnout")
                .description("Círculo para profesionales que buscan equilibrar su vida laboral y personal. Hablamos sobre límites saludables, desconexión digital, técnicas anti-burnout y cómo pedir ayuda cuando la carga es demasiada.")
                .creatorUserId("5") // Ramón
                .build();
        estresLaboral = communityRepository.save(estresLaboral);
        log.info("✅ Círculo creado: {}", estresLaboral.getName());

        memberRepository.save(CommunityMember.builder()
                .community(estresLaboral)
                .userId("5")
                .username("ramón")
                .anonymous(false)
                .role(CommunityMember.MemberRole.ADMIN)
                .build());

        memberRepository.save(CommunityMember.builder()
                .community(estresLaboral)
                .userId("4")
                .username("pedroff")
                .anonymous(false)
                .role(CommunityMember.MemberRole.MEMBER)
                .build());

        entryRepository.save(CommunityEntry.builder()
                .community(estresLaboral).authorUserId("5").type(CommunityEntry.EntryType.TIP)
                .content("Algo que me ayudó mucho: establecer una hora fija para dejar de mirar el correo del trabajo. Desde que puse ese límite a las 19h, mis noches son mucho más tranquilas. ⏰")
                .createdAt(LocalDateTime.now().minusDays(1)).build());

        entryRepository.save(CommunityEntry.builder()
                .community(estresLaboral).authorUserId("4").type(CommunityEntry.EntryType.PROPOSAL)
                .content("¿Qué les parece si compartimos cada viernes un 'logro de la semana'? Puede ser algo tan simple como haber dicho que no a una reunión innecesaria o haberse tomado un descanso.")
                .createdAt(LocalDateTime.now().minusHours(12)).build());

        // ========== CÍRCULO 5: Autoestima y Crecimiento Personal (creado por Pedro - userId: 4) ==========
        Community autoestima = Community.builder()
                .name("Autoestima y Crecimiento Personal")
                .creationReason("Fortalecer nuestra autoestima y trabajar en nuestro desarrollo personal")
                .description("Espacio para quienes quieren mejorar su relación consigo mismos. Compartimos ejercicios de autocompasión, afirmaciones positivas y celebramos cada paso de nuestro crecimiento.")
                .creatorUserId("4") // Pedro
                .build();
        autoestima = communityRepository.save(autoestima);
        log.info("✅ Círculo creado: {}", autoestima.getName());

        memberRepository.save(CommunityMember.builder()
                .community(autoestima)
                .userId("4")
                .username("pedroff")
                .anonymous(false)
                .role(CommunityMember.MemberRole.ADMIN)
                .build());

        entryRepository.save(CommunityEntry.builder()
                .community(autoestima).authorUserId("4").type(CommunityEntry.EntryType.ACHIEVEMENT)
                .content("🏆 Hoy me miré al espejo y me dije algo bonito en vez de criticarme. Parece tonto pero es un gran cambio para mí. La autocompasión es un músculo que se entrena.")
                .createdAt(LocalDateTime.now().minusHours(6)).build());

        log.info("✅ Datos de ejemplo cargados correctamente:");
        log.info("   - 5 círculos de apoyo creados");
        log.info("   - Laura (userId: 3) creó 2 círculos y está en 4 total");
        log.info("   - Ramón (userId: 5) creó 1 círculo y está en 3 total");
        log.info("   - Pedro (userId: 4) creó 2 círculos y está en 4 total");
        log.info("   - 10 publicaciones de ejemplo sobre salud mental");
    }
}