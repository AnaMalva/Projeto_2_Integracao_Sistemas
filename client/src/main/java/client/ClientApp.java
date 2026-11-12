package client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import soap.Researcher;
import soap.ResearchersWS;
import soap.ResearchersWSService;

public class ClientApp {

    private static final String REST_URL = "http://localhost:8080/rest/api/publications";

    private final ResearchersWS soap;   // SOAP (investigadores)
    private final Client rest;          // REST (publicacoes)
    private final Scanner in = new Scanner(System.in);

    public ClientApp() {
        soap = new ResearchersWSService().getResearchersWSPort();
        rest = ClientBuilder.newClient();
    }

    public static void main(String[] args) {
        new ClientApp().run();
    }

    // ---------------- Menu ----------------

    public void run() {
        while (true) {
            System.out.println();
            System.out.println("===== Projeto 2 - Cliente =====");
            System.out.println(" 1. Todos os investigadores                 (SOAP)");
            System.out.println(" 2. Investigador por nome                   (SOAP)");
            System.out.println(" 3. Investigadores por interesse            (SOAP)");
            System.out.println(" 4. Todas as publicacoes                    (REST)");
            System.out.println(" 5. Publicacao por titulo                   (REST)");
            System.out.println(" 6. Publicacoes de um investigador          (REST)");
            System.out.println(" 7. Publicacoes de uma revista/conferencia  (REST)");
            System.out.println(" 8. Revista/conferencia + investigador      (REST)");
            System.out.println(" 9. Citacoes por ano de um investigador");
            System.out.println("10. Publicacoes de uma instituicao num periodo");
            System.out.println(" 0. Sair");
            String op = ask("Opcao");

            try {
                switch (op) {
                    case "1" -> printResearchers(soap.getAllResearchers());
                    case "2" -> {
                        Researcher r = soap.getResearcherByName(ask("Nome do investigador"));
                        if (r == null) {
                            System.out.println("Investigador nao encontrado.");
                        } else {
                            printResearcher(r);
                        }
                    }
                    case "3" -> printResearchers(soap.getResearchersByInterest(ask("Interesse")));
                    case "4" -> printPublications(restAll());
                    case "5" -> {
                        Publication p = restByTitle(ask("Titulo"));
                        if (p == null) {
                            System.out.println("Publicacao nao encontrada.");
                        } else {
                            printPublication(p);
                        }
                    }
                    case "6" -> printPublications(restByResearcher(ask("Nome do investigador")));
                    case "7" -> printPublications(restByVenue(ask("Revista/conferencia")));
                    case "8" -> {
                        String venue = ask("Revista/conferencia");
                        String name = ask("Nome do investigador");
                        printPublications(restByVenueAndResearcher(venue, name));
                    }
                    case "9" -> citationsPerYear(ask("Nome do investigador"));
                    case "10" -> institutionInPeriod();
                    case "0" -> {
                        rest.close();
                        System.out.println("Adeus!");
                        return;
                    }
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private String ask(String label) {
        System.out.print(label + ": ");
        return in.nextLine().trim();
    }

    // ---------------- Chamadas ao REST ----------------

    private List<Publication> getList(WebTarget target) {
        Response response = target.request().get();
        try {
            if (response.getStatus() != 200) {
                return new ArrayList<>();
            }
            return response.readEntity(new GenericType<List<Publication>>() {});
        } finally {
            response.close();
        }
    }

    private List<Publication> restAll() {
        return getList(rest.target(REST_URL));
    }

    private Publication restByTitle(String title) {
        Response response = rest.target(REST_URL).path("title")
                .queryParam("title", title).request().get();
        try {
            if (response.getStatus() == 404) {
                return null;
            }
            return response.readEntity(Publication.class);
        } finally {
            response.close();
        }
    }

    private List<Publication> restByResearcher(String name) {
        return getList(rest.target(REST_URL).path("researcher").queryParam("name", name));
    }

    private List<Publication> restByVenue(String venue) {
        return getList(rest.target(REST_URL).path("venue").queryParam("name", venue));
    }

    private List<Publication> restByVenueAndResearcher(String venue, String name) {
        return getList(rest.target(REST_URL).path("venue-researcher")
                .queryParam("venue", venue).queryParam("researcher", name));
    }

    // ---------------- Operacoes extra ----------------

    // 9. Soma as citacoes das publicacoes do investigador, agrupadas por ano de publicacao
    private void citationsPerYear(String name) {
        List<Publication> pubs = restByResearcher(name);
        if (pubs.isEmpty()) {
            System.out.println("Nao ha publicacoes de " + name + ".");
            return;
        }
        Map<Integer, Integer> perYear = new TreeMap<>();   // TreeMap = ordenado por ano
        for (Publication p : pubs) {
            perYear.merge(p.getYear(), p.getCitations(), Integer::sum);
        }
        int total = 0;
        System.out.println("Citacoes por ano de " + name + ":");
        for (Map.Entry<Integer, Integer> e : perYear.entrySet()) {
            System.out.println("  " + e.getKey() + ": " + e.getValue());
            total += e.getValue();
        }
        System.out.println("  Total: " + total);
    }

    // 10. Investigadores da instituicao (SOAP) -> publicacoes de cada um (REST) -> filtra pelo periodo
    private void institutionInPeriod() {
        String institution = ask("Instituicao (ex: Coimbra)");
        int from = Integer.parseInt(ask("Desde o ano"));
        int to = Integer.parseInt(ask("Ate ao ano"));

        Map<String, Publication> result = new LinkedHashMap<>();   // pelo titulo, para nao repetir
        for (Researcher r : soap.getAllResearchers()) {
            boolean belongs = false;
            for (String u : r.getUniversities()) {
                if (u.toLowerCase().contains(institution.toLowerCase())) {
                    belongs = true;
                }
            }
            if (!belongs) {
                continue;
            }
            for (Publication p : restByResearcher(r.getName())) {
                if (p.getYear() >= from && p.getYear() <= to) {
                    result.putIfAbsent(p.getTitle(), p);
                }
            }
        }

        List<Publication> list = new ArrayList<>(result.values());
        list.sort((a, b) -> Integer.compare(a.getYear(), b.getYear()));
        System.out.println("Publicacoes de \"" + institution + "\" entre " + from + " e " + to + ":");
        printPublications(list);
    }

    // ---------------- Mostrar resultados ----------------

    private void printResearchers(List<Researcher> list) {
        if (list.isEmpty()) {
            System.out.println("Nenhum investigador encontrado.");
            return;
        }
        for (Researcher r : list) {
            printResearcher(r);
        }
        System.out.println("(" + list.size() + " investigadores)");
    }

    private void printResearcher(Researcher r) {
        System.out.println("- " + r.getName());
        System.out.println("    Instituicoes: " + r.getUniversities());
        System.out.println("    Interesses:   " + r.getInterests());
        if (r.getHomepage() != null) {
            System.out.println("    Homepage:     " + r.getHomepage());
        }
        if (r.getEmail() != null) {
            System.out.println("    Email:        " + r.getEmail());
        }
    }

    private void printPublications(List<Publication> list) {
        if (list.isEmpty()) {
            System.out.println("Nenhuma publicacao encontrada.");
            return;
        }
        for (Publication p : list) {
            printPublication(p);
        }
        System.out.println("(" + list.size() + " publicacoes)");
    }

    private void printPublication(Publication p) {
        System.out.println("- " + p.getTitle());
        System.out.println("    " + p.getYear() + " | " + p.getPublisher()
                + " | " + p.getCitations() + " citacoes | DOI: " + p.getDoi());
        System.out.println("    Autores: " + String.join(", ", p.getAuthors()));
    }
}