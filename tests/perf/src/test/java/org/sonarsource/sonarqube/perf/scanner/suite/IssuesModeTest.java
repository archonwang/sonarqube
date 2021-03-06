/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.sonarqube.perf.scanner.suite;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonarsource.sonarqube.perf.PerfRule;
import org.sonarsource.sonarqube.perf.PerfTestCase;

public class IssuesModeTest extends PerfTestCase {

  @ClassRule
  public static Orchestrator orchestrator = ScannerPerfTestSuite.ORCHESTRATOR;

  @ClassRule
  public static TemporaryFolder temp = new TemporaryFolder();

  @Rule
  public PerfRule perfRule = new PerfRule(4) {
    @Override
    protected void beforeEachRun() {
      orchestrator.resetData();
    }
  };

  @Before
  public void cleanDatabase() {
    orchestrator.resetData();
  }

  @Test
  public void issues_mode_scan_xoo_project() throws IOException {
    File userHome = temp.newFolder();
    orchestrator.getServer().provisionProject("sample", "xoo-sample");
    orchestrator.getServer().associateProjectToQualityProfile("sample", "xoo", "one-xoo-issue-per-line");
    SonarScanner runner = newScanner(
      "-Xmx512m -server",
      "sonar.analysis.mode", "issues",
      "sonar.userHome", userHome.getAbsolutePath(),
      "sonar.showProfiling", "true");
    long start = System.currentTimeMillis();
    orchestrator.executeBuild(runner, false);
    long duration = System.currentTimeMillis() - start;
    System.out.println("Issues analysis: " + duration + "ms");

    perfRule.assertDurationAround(duration, 3700L);
  }
}
