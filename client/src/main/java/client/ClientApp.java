package client;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.xml.ws.BindingProvider;
import soap.Researcher;
import soap.ResearchersWS;
import soap.ResearchersWSService;

public class ClientApp {

    // Server used when no address is given: java -jar client.jar
    private static final String DEFAULT_SERVER = "http://localhost:8080";

    private final String restUrl;       // e.g. http://localhost:8080/rest/api/publications
    private final ResearchersWS soap;   // SOAP (investigadores)
    private final Client rest;          // REST (publicacoes)
    private final Scanner in = new Scanner(System.in);

    public ClientApp(String server) throws MalformedURLException {
        // SOAP: read the WSDL from the given server and send the requests there too
        URL wsdl = URI.create(server + "/soap/ResearchersWS?wsdl").toURL();
        soap = new ResearchersWSService(wsdl).getResearchersWSPort();
        ((BindingProvider) soap).getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, server + "/soap/ResearchersWS");

        // REST
        restUrl = server + "/rest/api/publications";
        rest = ClientBuilder.newClient();
    }

    // Usage: java -jar client.jar                       (uses http://localhost:8080)
    //        java -jar client.jar http://otherpc:8080   (uses another server)
    public static void main(String[] args) {
        String server = DEFAULT_SERVER;
        if (args.length > 0) {
            server = args[0];
        }
        if (server.endsWith("/")) {
            server = server.substring(0, server.length() - 1);
        }

        try {
            new ClientApp(server).run();
        } catch (MalformedURLException | IllegalArgumentException e) {
            System.out.println("Endereco do servidor invalido: " + server);
            System.out.println("Exemplo: java -jar client.jar http://localhost:8080");
        } catch (RuntimeException e) {
            System.out.println("Nao foi possivel ligar ao servidor em " + server + ".");
            System.out.println("Confirme que o WildFly esta ligado e o project2.ear instalado.");
        }
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

            try {
                String op = ask("Opcao");

                switch (op) {
                    case "1":
                        printResearchers(soap.getAllResearchers());
                        break;
                    case "2": {
                        Researcher r = soap.getResearcherByName(ask("Nome do investigador"));
                        if (r == null) {
                            System.out.println("Investigador nao encontrado.");
                        } else {
                            printResearcher(r);
                        }
                        break;
                    }
                    case "3":
                        printResearchers(soap.getResearchersByInterest(ask("Interesse")));
                        break;
                    case "4":
                        printPublications(restAll());
                        break;
                    case "5": {
                        Publication p = restByTitle(ask("Titulo"));
                        if (p == null) {
                            System.out.println("Publicacao nao encontrada.");
                        } else {
                            printPublication(p);
                        }
                        break;
                    }
                    case "6":
                        printPublications(restByResearcher(ask("Nome do investigador")));
                        break;
                    case "7":
                        printPublications(restByVenue(ask("Revista/conferencia")));
                        break;
                    case "8": {
                        String venue = ask("Revista/conferencia");
                        String name = ask("Nome do investigador");
                        printPublications(restByVenueAndResearcher(venue, name));
                        break;
                    }
                    case "9":
                        citationsPerYear(ask("Nome do investigador"));
                        break;
                    case "10":
                        institutionInPeriod();
                        break;
                    case "0":
                        close();
                        return;
                    default:
                        System.out.println("Opcao invalida.");
                }
            } catch (NoSuchElementException e) {
                // End of input (Ctrl+D on Linux/Mac, Ctrl+Z + Enter on Windows): leave cleanly
                System.out.println();
                close();
                return;
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void close() {
        rest.close();
        System.out.println("Adeus!");
    }

    private String ask(String label) {
        System.out.print(label + ": ");
        return in.nextLine().trim();
    }

    // Asks until a valid year (a whole number) is written
    private int askYear(String label) {
        while (true) {
            String text = ask(label);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Escreva um ano valido, por exemplo 2015.");
            }
        }
    }

    // ---------------- Chamadas ao REST ----------------

    private List<Publication> getList(WebTarget target) {
        Response response = target.request().get();
        try {
            if (response.getStatus() != 200) {
                return new ArrayList<Publication>();
            }
            return response.readEntity(new GenericType<List<Publication>>() {});
        } finally {
            response.close();
        }
    }

    private List<Publication> restAll() {
        return getList(rest.target(restUrl));
    }

    private Publication restByTitle(String title) {
        Response response = rest.target(restUrl).path("title")
                .queryParam("title", title).request().get();
        try {
            if (response.getStatus() != 200) {
                return null;
            }
            return response.readEntity(Publication.class);
        } finally {
            response.close();
        }
    }

    private List<Publication> restByResearcher(String name) {
        return getList(rest.target(restUrl).path("researcher").queryParam("name", name));
    }

    private List<Publication> restByVenue(String venue) {
        return getList(rest.target(restUrl).path("venue").queryParam("name", venue));
    }

    private List<Publication> restByVenueAndResearcher(String venue, String name) {
        return getList(rest.target(restUrl).path("venue-researcher")
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
        Map<Integer, Integer> perYear = new TreeMap<Integer, Integer>();   // TreeMap = ordenado por ano
        for (Publication p : pubs) {
            Integer current = perYear.get(p.getYear());
            if (current == null) {
                current = 0;
            }
            perYear.put(p.getYear(), current + p.getCitations());
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
        int from = askYear("Desde o ano");
        int to = askYear("Ate ao ano");
        while (to < from) {
            System.out.println("O ano final tem de ser igual ou posterior a " + from + ".");
            to = askYear("Ate ao ano");
        }

        Map<String, Publication> result = new LinkedHashMap<String, Publication>();   // pelo titulo, para nao repetir
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

        List<Publication> list = new ArrayList<Publication>(result.values());
        Collections.sort(list, new Comparator<Publication>() {
            @Override
            public int compare(Publication a, Publication b) {
                return Integer.compare(a.getYear(), b.getYear());
            }
        });
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