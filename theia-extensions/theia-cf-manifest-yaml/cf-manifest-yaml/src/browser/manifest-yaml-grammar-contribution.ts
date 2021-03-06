import {
    CF_MANIFEST_YAML_LANGUAGE_ID,
    CF_MANIFEST_YAML_LANGUAGE_NAME
} from '../common';
import {LanguageGrammarDefinitionContribution, TextmateRegistry} from '@theia/monaco/lib/browser/textmate';
import {injectable} from 'inversify';
import {YAML_LANGUAGE_GRAMMAR_SCOPE, YAML_CONFIG} from '@pivotal-tools/theia-languageclient/lib/common';

@injectable()
export class ManifestYamlGrammarContribution implements LanguageGrammarDefinitionContribution {

    registerTextmateLanguage(registry: TextmateRegistry) {
        monaco.languages.register({
            id: CF_MANIFEST_YAML_LANGUAGE_ID,
            aliases: [
                CF_MANIFEST_YAML_LANGUAGE_NAME
            ],
            filenamePatterns: ['*manifest*.yml']
        });

        monaco.languages.setLanguageConfiguration(CF_MANIFEST_YAML_LANGUAGE_ID, YAML_CONFIG);

        registry.mapLanguageIdToTextmateGrammar(CF_MANIFEST_YAML_LANGUAGE_ID, YAML_LANGUAGE_GRAMMAR_SCOPE);
    }
}
