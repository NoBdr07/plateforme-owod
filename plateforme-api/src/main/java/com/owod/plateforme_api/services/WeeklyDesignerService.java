package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.WeeklyDesigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Random;

@Service
public class WeeklyDesignerService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DesignerService designerService;

    // @Scheduled(cron = "0 0 18 * * *") pour le remettre à une heure souhaitée, ne fonctionne pas si back pas en cours à l'heure donnée
    //@Scheduled(cron = "0 0 0 * * MON") // Exécution tous les lundis à 00:00
    public void selectNewWeeklyDesigner() {
        List<Designer> allDesigners = designerService.getAll();

        if (!allDesigners.isEmpty()) {
            // Récupérer le dernier WeeklyDesigner
            WeeklyDesigner previousWeeklyDesigner = getLastWeeklyDesigner();

            if (previousWeeklyDesigner != null) {
                String previousDesignerId = previousWeeklyDesigner.getDesignerId();

                // Exclure le designer de la semaine précédente
                allDesigners = allDesigners.stream()
                        .filter(designer -> !designer.getId().equals(previousDesignerId))
                        .toList();
            }

            if (!allDesigners.isEmpty()) {
                // Sélectionner un designer aléatoire parmi les restants
                Random random = new Random();
                Designer selectedDesigner = allDesigners.get(random.nextInt(allDesigners.size()));

                // Créer un nouvel enregistrement pour le designer sélectionné
                WeeklyDesigner weeklyDesigner = new WeeklyDesigner();
                weeklyDesigner.setDesignerId(selectedDesigner.getId());

                LocalDateTime now = LocalDateTime.now();
                weeklyDesigner.setStartDate(now);
                weeklyDesigner.setEndDate(now.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));

                mongoTemplate.save(weeklyDesigner);
            }
        }
    }


    public WeeklyDesigner getLastWeeklyDesigner() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "startDate")); // Trier par date de début décroissante
        return mongoTemplate.findOne(query, WeeklyDesigner.class);
    }


}
