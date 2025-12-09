#!/usr/bin/env groovy

def jenkinsfile

def overrides = [
        scriptVersion              : 'v7',
        pipelineScript             : 'https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git',
        javaVersion                : 21,
        iqOrganizationName         : 'Team RST',
        iqCredentialsId            : 'ci_tdg_public',
        iq                         : false,
        sonarQube                  : true,
        sonarBreakOnQualityGate    : true,
        iqBreakOnUnstable          : false,
        compileProperties          : "-U",
        pomPath                    : 'testdatagenerator/pom.xml',
        openShiftBuild             : true,
        jacoco                     : true,
        versionStrategy            : [
                [branch: 'master', versionHint: '1']
        ]
]

fileLoader.withGit(overrides.pipelineScript, overrides.scriptVersion) {
    jenkinsfile = fileLoader.load('templates/leveransepakke')
}
jenkinsfile.maven(overrides.scriptVersion, overrides)
