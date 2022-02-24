package fr.gamaticow.hashcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * Created by Corentin on 24/02/2022 at 19:31
 */

public class Main {

    public static DataSet dataSet;

    public static void main(String[] args) {
        dataSet = new InputReader("b_better_start_small.in.txt").getDataSet();

        Indexing indexing = new Indexing(dataSet);

        PriorityQueue<Project> projects = new PriorityQueue<>();
        for(Project project : dataSet.projects) {
            float importance =  (project.bestBefore - project.nbDays)*1f/project.score;
            //System.out.println("(" + project.bestBefore + "-" + project.nbDays + ")/" + project.score + "=" + importance);
            project.priority = importance;
            projects.add(project);
        }

        int day = -1;
        int score = 0;

        int total = 0;
        String output = "";

        List<ActiveProject> runningProjects = new ArrayList<>();

        boolean wait = false;

        while ((!projects.isEmpty() || !runningProjects.isEmpty()) && !wait) {
            System.out.println("Project left " + projects.size() + " / Project running " + runningProjects.size());

            day++;

            List<ActiveProject> remove = new ArrayList<>();
            for(ActiveProject activeProject : runningProjects) {
                if(activeProject.hasEnded(day)) {
                    total++;
                    output += activeProject.getOutput() + "\n";
                    score += activeProject.end(day);
                    remove.add(activeProject);
                }
            }
            runningProjects.removeAll(remove);

            if(runningProjects.isEmpty()) {
                wait = true;
            }

            List<Project> waitingProjects = new ArrayList<>();

            Project project;
            while ((project = projects.poll()) != null) {
                //System.out.println(p.name + " " + p.priority);
                ActiveProject activeProject = new ActiveProject(project);

                List<Role> l1Role = Arrays.stream(project.roles).filter(role -> role.level == 1).collect(Collectors.toList());
                boolean[] l1Affected = new boolean[l1Role.size()];

                List<Role> emptyRole = new ArrayList<>();

                for(int r = 0; r < project.roles.length; r++) {
                    Role role = project.roles[r];
                    if(l1Role.contains(role))
                        continue;

                    boolean found = false;
                    int level = role.level;

                    if(activeProject.canMentor(role.skill, level))
                        level--;

                    List<Contributor> skilled = removeLowLevel(filterFree(indexing.getContributorsBySkill(role.skill)), role.skill, level);
                    if(skilled.size() == 0) {
                        emptyRole.add(role);
                        continue;
                    }

                    for(int i = 0; i < l1Role.size(); i++) {
                        Role rl1 = l1Role.get(i);
                        List<Contributor> doubleSkill = filterFree(innerJoin(skilled, indexing.getContributorsBySkill(rl1.skill)));
                        if(doubleSkill.size() > 0) {
                            activeProject.assign(doubleSkill.get(0), role);
                            found = true;
                            l1Affected[i] = true;
                            break;
                        }
                    }

                    if(!found) {
                        activeProject.assign(skilled.get(0), role);
                    }
                }

                for(Role role : emptyRole) {
                    int level = role.level;

                    if(activeProject.canMentor(role.skill, level))
                        level--;

                    List<Contributor> skilled = removeLowLevel(filterFree(indexing.getContributorsBySkill(role.skill)), role.skill, level);
                    if(!skilled.isEmpty()) {
                        activeProject.assign(skilled.get(0), role);
                    }
                }

                for(int i = 0; i < l1Affected.length; i++) {
                    Role role = l1Role.get(i);
                    if(!l1Affected[i]) {
                        int level = role.level;

                        if(activeProject.canMentor(role.skill, level))
                            level--;

                        List<Contributor> skilled = removeLowLevel(filterFree(indexing.getContributorsBySkill(role.skill)), role.skill, level);
                        if(!skilled.isEmpty()) {
                            activeProject.assign(skilled.get(0), role);
                        }
                    } else {
                        List<Contributor> free = freeContributor();
                        if(!free.isEmpty()) {
                            activeProject.assign(free.get(0), role);
                        }
                    }
                }

                if(activeProject.canStart()) {
                    activeProject.start(day);
                    runningProjects.add(activeProject);
                } else {
                    activeProject.cancel();
                    waitingProjects.add(project);
                }

            }

            projects.addAll(waitingProjects);

            if(runningProjects.size() > 0)
                wait = false;

        }

        System.out.println(score);
        System.out.println(total + "\n" + output);
    }

    private static List<Contributor> removeLowLevel(List<Contributor> l, String skill, int level) {
        if(level == 0)
            return Arrays.asList(dataSet.contributors);
        return l.stream().filter(contributor -> Arrays.stream(contributor.skills).filter(s -> s.name.equals(skill)).findFirst().orElse(new Skill()).level >= level).collect(Collectors.toList());
    }

    private static List<Contributor> innerJoin(List<Contributor> l1, List<Contributor> l2) {
        return l1.stream().filter(l2::contains).collect(Collectors.toList());
    }

    private static List<Contributor> freeContributor() {
        return Arrays.stream(dataSet.contributors).filter(contributor -> contributor.free).collect(Collectors.toList());
    }

    private static List<Role> canMentor(Project project, Skill skill) {
        return Arrays.stream(project.roles).filter(role -> role.skill.equals(skill.name) && role.level <= skill.level-1).collect(Collectors.toList());
    }

    private static List<Contributor> filterFree(List<Contributor> l) {
        return l.stream().filter(contributor -> contributor.free).collect(Collectors.toList());
    }

}
