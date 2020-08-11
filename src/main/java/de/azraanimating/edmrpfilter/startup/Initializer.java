/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.startup;

import de.azraanimating.edmrpfilter.main.EliteDangerousMiningRoutePlanner;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.IOException;

public class Initializer {

    public static void main(final String[] args) {

        try {
            final EliteDangerousMiningRoutePlanner eliteDangerousMiningRoutePlanner = new EliteDangerousMiningRoutePlanner();
        } catch (final InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }


    }

}
