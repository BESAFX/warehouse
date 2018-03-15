package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Team;
import com.besafx.app.service.TeamService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api/team/")
public class TeamRest {

    private final static Logger log = LoggerFactory.getLogger(BranchRest.class);

    private final String FILTER_TABLE = "**,persons[id]";

    @Autowired
    private TeamService teamService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TEAM_CREATE')")
    public String create(@RequestBody Team team, Principal principal) {
        Team topTeam = teamService.findTopByOrderByCodeDesc();
        if (topTeam == null) {
            team.setCode(1);
        } else {
            team.setCode(topTeam.getCode() + 1);
        }
        team = teamService.save(team);
        notificationService.notifyAll(Notification.builder().message("تم مجموعة صلاحيات جديدة بنجاح").type("success").build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), team);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TEAM_UPDATE')")
    public String update(@RequestBody Team team, Principal principal) {
        if (teamService.findByCodeAndIdIsNot(team.getCode(), team.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Team object = teamService.findOne(team.getId());
        if (object != null) {
            team = teamService.save(team);
            notificationService.notifyAll(Notification.builder().message("تم تعديل بيانات المجموعة بنجاح").type("success").build());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), team);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TEAM_DELETE')")
    public void delete(@PathVariable Long id, Principal principal) {
        Team team = teamService.findOne(id);
        if (team != null) {
            if (!team.getPersons().isEmpty()) {
                throw new CustomException("لا يمكن حذف هذة المجموعة لإعتماد بعض المستخدمين عليها.");
            }
            teamService.delete(id);
            notificationService.notifyAll(Notification.builder().message("تم حذف مجموعة الصلاحيات بنجاح").type("error").build());
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(teamService.findAll()));
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), teamService.findOne(id));
    }
}
