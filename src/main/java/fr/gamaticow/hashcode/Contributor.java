package fr.gamaticow.hashcode;

/**
 * Created by Corentin on 24/02/2022 at 19:21
 */

public class Contributor {

    public String name;
    public Skill[] skills;

    public boolean free = true;

    public void addLevel(Role role, boolean force) {
        for(Skill s : skills) {
            if(s.name.equals(role.skill) && (force || s.level <= role.level))
                s.level++;
        }
    }

}
