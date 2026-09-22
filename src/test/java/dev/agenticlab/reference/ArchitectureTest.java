package dev.agenticlab.reference;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Testes de referência da arquitetura (AD-1, AD-2, AD-8, AD-10).
 *
 * <p>As regras aqui são estruturais e valem mesmo antes de {@code account} e
 * {@code transfer} existirem: com zero classes correspondentes, elas são
 * satisfeitas por ausência de violação (ver {@code archunit.properties}).
 * Nenhum pacote ou classe é criado artificialmente para "alimentar" a régua.
 */
@AnalyzeClasses(packages = "dev.agenticlab", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule web_should_not_access_repository_directly =
            noClasses()
                    .that()
                    .resideInAPackage("..web..")
                    .should()
                    .accessClassesThat()
                    .resideInAPackage("..repository..")
                    .because("AD-1: web fala com service; repository é acessado só pelo service");

    @ArchTest
    static final ArchRule transfer_should_not_access_account_repository_or_model =
            noClasses()
                    .that()
                    .resideInAPackage("..transfer..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..account.repository..", "..account.model..")
                    .because(
                            "AD-2: account é o único dono do saldo; transfer usa só o serviço"
                                    + " público de account");

    @ArchTest
    static final ArchRule application_packages_should_be_free_of_cycles =
            slices().matching("dev.agenticlab.(*)..").should().beFreeOfCycles();
}
