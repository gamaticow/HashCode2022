package fr.gamaticow.hashcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by Corentin on 24/02/2022 at 21:21
 */

public class ActiveProject {

    public final Project project;
    private final Contributor[] contributors;
    private List<Skill> mentoredSkills = new ArrayList<>();
    private int startDay = 0;

    public ActiveProject(Project project) {
        this.project = project;
        contributors = new Contributor[project.roles.length];
    }

    public void assign(Contributor contributor, Role role) {
        int index = Arrays.asList(project.roles).indexOf(role);
        contributors[index] = contributor;
        contributor.free = false;

        for(Skill skill : contributor.skills) {
            Skill s = new Skill();
            s.name = skill.name;
            s.level = skill.level-1;
            mentoredSkills.add(s);
        }
    }

    public boolean canMentor(String skill, int level) {
        return mentoredSkills.stream().filter(s -> s.name.equals(skill)).anyMatch(s -> s.level > level);
    }

    public boolean canStart() {
        return Arrays.stream(contributors).allMatch(Objects::nonNull);
    }

    public void start(int day) {
        this.startDay = day;
    }

    public void cancel() {
        Arrays.stream(contributors).forEach(contributor -> {
            if(contributor != null)
                contributor.free = true;
        });
    }

    public boolean hasEnded(int day) {
        return day - startDay >= project.nbDays;
    }

    public int end(int day) {
        Arrays.stream(contributors).forEach(contributor -> {
            if(contributor != null)
                contributor.free = true;
        });

        int daysLate = day - project.bestBefore;
        int score = project.score;
        if(daysLate > 0) {
            score -= daysLate;
        }

        for(int i = 0; i < contributors.length; i++) {
            Contributor contributor = contributors[i];
            Role role = project.roles[i];
            contributor.addLevel(role, score == 0);
        }

        return score;
    }

    public String getOutput() {
        String c = "";
        for(Contributor contributor : contributors) {
            if(c.length() > 0)
                c += " ";
            c += contributor.name;
        }
        return project.name + "\n" + c;
    }

}
