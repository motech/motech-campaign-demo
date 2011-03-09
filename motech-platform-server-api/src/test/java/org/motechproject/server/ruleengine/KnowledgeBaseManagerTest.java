/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.motechproject.server.ruleengine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.drools.KnowledgeBase;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Test;
import org.motechproject.dao.RuleRepository;
import org.motechproject.model.Rule;
import org.motechproject.server.osgi.OsgiFrameworkService;

public class KnowledgeBaseManagerTest {

    @Test
    public void addOrUpdateRuleTest() throws Exception {
        String ruleFolder = "/rules";
        String ruleFile = "test.drl";
        
        KnowledgeBaseManager kbm = KnowledgeBaseManager.getInstance();
        RuleRepository repo = mock(RuleRepository.class);
        OsgiFrameworkService ofs = mock(OsgiFrameworkService.class);
        kbm.setOsgiFrameworkService(ofs);
        kbm.setRuleRepository(repo);
        
        File file = new File(getClass().getResource(ruleFolder + "/" + ruleFile).getFile());
        
        kbm.addOrUpdateRule(file);
        
        verify(repo).contains(ruleFile);
        verify(repo).add(any(Rule.class));
        
        KnowledgeBase kbase = kbm.getKnowledgeBase(ruleFile);
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
        Applicant applicant = new Applicant( "Mr John Smith", 16 );
        assertTrue( applicant.isValid() );
        ksession.execute( applicant );
        assertFalse( applicant.isValid() );
    }
    
}
