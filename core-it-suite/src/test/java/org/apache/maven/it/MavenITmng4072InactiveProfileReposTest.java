package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.shared.verifier.util.ResourceExtractor;
import org.apache.maven.shared.verifier.Verifier;

import java.io.File;
import java.util.Properties;

import org.junit.jupiter.api.Test;

/**
 * This is a test set for <a href="https://issues.apache.org/jira/browse/MNG-4072">MNG-4072</a>.
 *
 * @author Benjamin Bentmann
 */
public class MavenITmng4072InactiveProfileReposTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng4072InactiveProfileReposTest()
    {
        super( ALL_MAVEN_VERSIONS );
    }

    /**
     * Test that repositories from inactive profiles are actually not used for artifact resolution.
     *
     * @throws Exception in case of failure
     */
    @Test
    public void testit()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-4072" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteArtifacts( "org.apache.maven.its.mng4072" );
        Properties filterProps = verifier.newDefaultFilterProperties();
        verifier.filterFile( "pom-template.xml", "pom.xml", "UTF-8", filterProps );
        verifier.filterFile( "profiles-template.xml", "profiles.xml", "UTF-8", filterProps );
        verifier.filterFile( "settings-template.xml", "settings.xml", "UTF-8", filterProps );
        verifier.addCliOption( "--settings" );
        verifier.addCliOption( "settings.xml" );
        try
        {
            verifier.addCliArgument( "validate" );
            verifier.execute();
            verifier.verifyErrorFreeLog();
            fail( "Dependency resolution succeeded although all profiles are inactive" );
        }
        catch ( Exception e )
        {
            // expected, all profiles are inactive, hence the repos inaccessible
        }
    }

}
