package fr.gamaticow.hashcode;

import java.io.*;

/**
 * Created by Corentin on 24/02/2022 at 19:15
 */

public class InputReader {

    private final DataSet dataSet = new DataSet();

    public InputReader(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            ReadingState state = ReadingState.INIT;
            int contributors = 0;
            int projects = 0;
            int cpt = 0;

            Contributor contributor = null;
            Project project = null;

            while (reader.ready()) {
                String[] args = reader.readLine().split(" ");
                if(state == ReadingState.INIT) {
                    contributors = Integer.parseInt(args[0]);
                    projects = Integer.parseInt(args[1]);
                    dataSet.contributors = new Contributor[contributors];
                    dataSet.projects = new Project[projects];
                    state = ReadingState.CONTRIBUTORS;
                } else if(state == ReadingState.CONTRIBUTORS) {
                    contributor = new Contributor();
                    dataSet.contributors[dataSet.contributors.length-contributors] = contributor;
                    contributor.name = args[0];
                    cpt = Integer.parseInt(args[1]);
                    contributor.skills = new Skill[cpt];
                    state = ReadingState.SKILLS;
                } else if(state == ReadingState.SKILLS) {
                    Skill skill = new Skill();
                    skill.name = args[0];
                    skill.level = Integer.parseInt(args[1]);
                    contributor.skills[contributor.skills.length-cpt] = skill;
                    if(--cpt == 0) {
                        if(--contributors == 0) {
                            state = ReadingState.PROJECTS;
                        } else {
                            state = ReadingState.CONTRIBUTORS;
                        }
                    }
                } else if(state == ReadingState.PROJECTS) {
                    project = new Project();
                    dataSet.projects[dataSet.projects.length-projects] = project;
                    project.name = args[0];
                    project.nbDays = Integer.parseInt(args[1]);
                    project.score = Integer.parseInt(args[2]);
                    project.bestBefore = Integer.parseInt(args[3]);
                    cpt = Integer.parseInt(args[4]);
                    project.roles = new Role[cpt];
                    state = ReadingState.ROLES;
                } else if(state == ReadingState.ROLES) {
                    Role role = new Role();
                    role.skill = args[0];
                    role.level = Integer.parseInt(args[1]);
                    project.roles[project.roles.length-cpt] = role;
                    if(--cpt == 0) {
                        if(--projects == 0) {
                            state = ReadingState.END;
                        } else {
                            state = ReadingState.PROJECTS;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    private enum ReadingState {
        INIT,
        CONTRIBUTORS,
        SKILLS,
        PROJECTS,
        ROLES,
        END
    }

}
