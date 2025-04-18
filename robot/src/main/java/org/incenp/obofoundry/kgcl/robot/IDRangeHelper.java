/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2024 Damien Goutte-Gattat
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Gnu General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.incenp.obofoundry.kgcl.robot;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.incenp.obofoundry.dicer.IDRange;
import org.incenp.obofoundry.dicer.IDRangePolicy;
import org.incenp.obofoundry.dicer.IDRangePolicyReader;
import org.incenp.obofoundry.dicer.InvalidIDRangePolicyException;
import org.incenp.obofoundry.kgcl.IAutoIDGenerator;
import org.incenp.obofoundry.kgcl.RandomizedIDGenerator;
import org.incenp.obofoundry.kgcl.SequentialIDGenerator;
import org.semanticweb.owlapi.model.OWLOntology;

/*
 * Helper methods to work with OBO-style ID range policy files.
 */
public class IDRangeHelper {

    /**
     * Finds an ID range policy file in the current directory. This methods looks
     * for a single file whose name ends with {@code -idranges.owl} in the current
     * directory.
     * 
     * @return The name of the ID range policy file, if such a file exists;
     *         otherwise {@code null}.
     */
    public static String findIDRangeFile() {
        FilenameFilter idRangeFilter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.endsWith("-idranges.owl");
            }
        };

        File cwd = new File(".");
        String[] rangeFiles = cwd.list(idRangeFilter);
        if ( rangeFiles.length == 1 ) {
            return rangeFiles[0];
        }

        return null;
    }

    /**
     * Gets an ID generator initialised with data from the specified range file.
     * 
     * @param ontology  The ontology to generate IDs for.
     * @param rangeFile The range file to initialise the generator with.
     * @param rangeName The name of the range to use. This should match the
     *                  {@code allocatedto} annotation of one of the ranges in the
     *                  file. May be {@code null}, in which case the method will
     *                  look for a range that matches one of the alternative names
     *                  instead. If not {@code null} and a corresponding range
     *                  cannot be found, an exception will be thrown.
     * @param altNames  A list of default range names to use if {@code rangeName} is
     *                  {@code null}.
     * @param random    If {@code true}, the generator will produce IDs picked at
     *                  random within the target range; otherwise, IDs will be
     *                  picked sequentially.
     * @return A newly initialised ID generator.
     * @throws InvalidIDRangePolicyException If the file is not a valid ID range
     *                                       policy file, if the requested
     *                                       {@code rangeName} cannot be found, or
     *                                       if none of the default ranges can be
     *                                       found.
     * @throws IOException                   If the range file cannot be read for
     *                                       any reason.
     */
    public static IAutoIDGenerator getIDGenerator(OWLOntology ontology, String rangeFile, String rangeName,
            String[] altNames, boolean random) throws IOException, InvalidIDRangePolicyException {

        IDRangePolicyReader reader = new IDRangePolicyReader();
        IDRangePolicy policy = reader.read(rangeFile);
        IDRange range = null;

        if ( rangeName != null ) {
            range = policy.getRangeFor(rangeName);
            if ( range == null ) {
                throw new InvalidIDRangePolicyException("Requested range not found in ID range file");
            }
        }

        int i = 0;
        while ( range == null && altNames != null && i < altNames.length ) {
            range = policy.getRangeFor(altNames[i++]);
        }

        if ( range == null ) {
            throw new InvalidIDRangePolicyException("No suitable range found in ID range file");
        }

        String format = String.format("%s%%0%dd", policy.getPrefix(), policy.getWidth());
        if ( random ) {
            return new RandomizedIDGenerator(ontology, format, range.getLowerBound(), range.getUpperBound());
        } else {
            return new SequentialIDGenerator(ontology, format, range.getLowerBound(), range.getUpperBound());
        }
    }

    /**
     * Gets an ID generator initialised with data from the specified range file, or
     * a default file. This method is similar to
     * {@link #getIDGenerator(OWLOntology, String, String, String[], boolean)},
     * except that if the {@code rangeFile} parameter is {@code null}, it tries to
     * find a default range file (using the {@link #findIDRangeFile()} method); in
     * that case, no exception will be thrown.
     * 
     * @param ontology  The ontology to generate IDs for.
     * @param rangeFile The range file to initialise the generator with. If
     *                  {@code null}, the method will try to find a default file.
     * @param rangeName The name of the range to use. This should match the
     *                  {@code allocatedto} annotation of one of the ranges in the
     *                  file. May be {@code null}, in which case the method will
     *                  look for a range that matches one of the alternative names
     *                  instead.
     * @param altNames  A list of default range names to use if {@code rangeName} is
     *                  {@code null}.
     * @param random    If {@code true}, the generator will produce IDs picked at
     *                  random within the target range; otherwise, IDs will be
     *                  picked sequentially.
     * @return A newly initialised ID generator or {@code null} if no range file has
     *         been specified and no suitable default file was found.
     * @throws InvalidIDRangePolicyException If the file is not a valid ID range
     *                                       policy file, if the requested
     *                                       {@code rangeName} cannot be found, or
     *                                       if none of the default ranges can be
     *                                       found.
     * @throws IOException                   If the range file cannot be read for
     *                                       any reason
     */
    public static IAutoIDGenerator maybeGetIDGenerator(OWLOntology ontology, String rangeFile, String rangeName,
            String[] altNames, boolean random) throws IOException, InvalidIDRangePolicyException {
        boolean silent = false;
        IAutoIDGenerator generator = null;

        if ( rangeFile == null ) {
            rangeFile = findIDRangeFile();
            if ( rangeFile == null ) {
                return null;
            }
            silent = true;
        }

        try {
            generator = getIDGenerator(ontology, rangeFile, rangeName, altNames, random);
        } catch ( InvalidIDRangePolicyException e ) {
            if ( !silent ) {
                throw e;
            }
        }

        return generator;
    }
}
