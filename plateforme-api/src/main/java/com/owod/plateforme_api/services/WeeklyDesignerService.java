package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.Designer;
import com.owod.plateforme_api.models.entities.WeeklyDesigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Criteria;

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

    @Scheduled(cron = "0 0 18 * * *")
    public void selectNewWeeklyDesigner() {
        List<Designer> allDesigners = designerService.getAll();

        if(!allDesigners.isEmpty()) {
            Random random = new Random();
            Designer selectedDesigner = allDesigners.get(random.nextInt(allDesigners.size()));

            WeeklyDesigner weeklyDesigner = new WeeklyDesigner();
            weeklyDesigner.setDesignerId(selectedDesigner.getId());

            LocalDateTime now = LocalDateTime.now();
            weeklyDesigner.setStartDate(now);
            weeklyDesigner.setEndDate(now.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));

            mongoTemplate.save(weeklyDesigner);
        }
    }

    public WeeklyDesigner getCurrentWeeklyDesigner() {
        LocalDateTime now = LocalDateTime.now();
        Query query = new Query();
        query.addCriteria(Criteria.where("startDate").lte(now).and("endDate").gt(now));
        return mongoTemplate.findOne(query, WeeklyDesigner.class);
    }


}
