package com.GoGoGo_Search.SearchEngine;

import com.GoGoGo_Search.SearchEngine.LinkRepository.LinkRepo;
import com.GoGoGo_Search.SearchEngine.document.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rest/links")
public class LinkResource {
    // pages
    public String HomePage() {
        String temp = "<!DOCTYPE html>\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\">\n" + "\n"
                + "<head>\n" + "    <link rel=\"stylesheet\" th:href=\"@{/css/index.css}\" />\n"
                + "    <meta charset=\"UTF-8\" />\n"
                + "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n"
                + "    <link rel=\"icon\" href=\"Group_2_1.png\" />\n"
                + "    <link rel=\"stylesheet\" href=\"index.css\" />\n" + "    <title>GoGoGo</title>\n" + "</head>\n"
                + "\n" + "<body>\n" + "    <main>\n" + "        <div class=\"Decor\">\n"
                + "            <img class=\"LeftBrac\" src=\"Group_8.png\" />\n"
                + "            <a><img class=\"Logo\" src=\"Group_2_1.png\" /></a>\n"
                + "            <img class=\"RightBrac\" src=\"Group_7_bb.png\" />\n" + "        </div>\n"
                + "        <form class=\"Form\" action=\"http://localhost:8093/rest/links/findKey\" method=\"GET\">\n"
                + "\n" + "            <input type=\"text\" name=\"searchSentence\" id=\"searchfield\" />\n"
                + "            <label id=\"searchnotes\" for=\"searchfield\"></label>\n"
                + "            <!--<input type=\"text\" name=\"\" id=\"speechToText\" placeholder=\"Speak Something\" onfocus=\"record()\">-->\n"
                + "            <div class=\"LowBar\">\n" + "                <div class=\"Options\">\n"
                + "                    <button type=\"button\" class=\"icon\" onfocus=\"record()\">\n"
                + "              <img src=\"microphone.png\" alt=\"\" />\n" + "            </button> |\n"
                + "                    <input type=\"radio\" name=\"mode\" id=\"modeAnd\" style=\"display: none\" value=\"And\" />\n"
                + "                    <label for=\"modeAnd\">\n"
                + "              <button type=\"button\" class=\"icon\" id=\"AndIcon\" onclick=\"And()\">\n"
                + "                <img src=\"img_476874_30302.png\" alt=\"\" />\n" + "              </button>\n"
                + "            </label>\n"
                + "                    <input type=\"radio\" name=\"mode\" id=\"modeOr\" style=\"display: none\" value=\"Or\" />\n"
                + "                    <label for=\"modeOr\">\n"
                + "              <button type=\"button\" class=\"icon\" id=\"OrIcon\" onclick=\"Or()\">\n"
                + "                <img src=\"881194-200.png\" alt=\"\" />\n" + "              </button>\n"
                + "            </label> |\n"
                + "                    <button type=\"button\" class=\"icon\" id=\"EquationIcon\" onclick=\"Equation()\">\n"
                + "              <img src=\"Eq.png\" alt=\"\" />\n" + "            </button> |\n"
                + "                    <button type=\"button\" class=\"icon\" id=\"DeleteIcon\" onclick=\"document.getElementById('searchfield').value = '';\">\n"
                + "              <img src=\"delete.png\" alt=\"\" />\n" + "            </button> |\n"
                + "                </div>\n"
                + "                <button id=\"searchButton\" type=\"button\" onclick=\"SubmitOrCalc()\">\n"
                + "            Go\n" + "          </button>\n" + "            </div>\n" + "        </form>\n"
                + "    </main>\n" + "</body>\n"
                + "<script src=\"https://cdn.jsdelivr.net/npm/nerdamer@latest/nerdamer.core.js\"></script>\n"
                + "<script src=\"https://cdn.jsdelivr.net/npm/nerdamer@latest/Algebra.js\"></script>\n"
                + "<script src=\"https://cdn.jsdelivr.net/npm/nerdamer@latest/Calculus.js\"></script>\n"
                + "<script src=\"https://cdn.jsdelivr.net/npm/nerdamer@latest/Solve.js\"></script>\n" + "<script>\n"
                + "    function record() {\n" + "        var recognition = new webkitSpeechRecognition();\n"
                + "        recognition.lang = \"en-GB\";\n" + "\n"
                + "        recognition.onresult = function(event) {\n"
                + "            document.getElementById(\"searchfield\").value +=\n"
                + "                event.results[0][0].transcript + \" \";\n" + "        };\n"
                + "        recognition.start();\n" + "    }\n" + "    var eq = true;\n" + "    Equation();\n"
                + "    And();\n" + "\n" + "    function And() {\n"
                + "        document.querySelector(\"#modeAnd\").setAttribute(\"checked\", true);\n"
                + "        document.querySelector(\"#modeOr\").removeAttribute(\"checked\");\n"
                + "        document.querySelector(\"#AndIcon\").setAttribute(\"checked\", true);\n"
                + "        document.querySelector(\"#OrIcon\").removeAttribute(\"checked\");\n" + "    }\n" + "\n"
                + "    function Or() {\n"
                + "        document.querySelector(\"#modeOr\").setAttribute(\"checked\", true);\n"
                + "        document.querySelector(\"#modeAnd\").removeAttribute(\"checked\");\n"
                + "        document.querySelector(\"#OrIcon\").setAttribute(\"checked\", true);\n"
                + "        document.querySelector(\"#AndIcon\").removeAttribute(\"checked\");\n" + "    }\n" + "\n"
                + "    function Equation() {\n" + "        eq = !eq;\n" + "        if (eq) {\n"
                + "            document\n" + "                .getElementById(\"searchfield\")\n"
                + "                .setAttribute(\n" + "                    \"placeholder\",\n"
                + "                    \"Enter equation and the variable to solve for or a system of equations\"\n"
                + "                );\n" + "            document.getElementById(\"searchnotes\").innerHTML =\n"
                + "                \"E.g.: 'x^2-8x+1, x' or '3*x^2/y=2, z*x*y-1=35, 5*z^2+7=52'\";\n"
                + "            document.querySelector(\"#EquationIcon\").setAttribute(\"checked\", true);\n"
                + "        } else {\n" + "            document\n" + "                .getElementById(\"searchfield\")\n"
                + "                .setAttribute(\"placeholder\", \"Enter Keyword\");\n"
                + "            document.getElementById(\"searchnotes\").innerHTML = \"\";\n"
                + "            document.querySelector(\"#EquationIcon\").removeAttribute(\"checked\");\n"
                + "        }\n" + "    }\n" + "\n" + "    function SubmitOrCalc() {\n" + "        if (eq) {\n"
                + "            calc();\n" + "        } else {\n"
                + "            if (document.getElementById(\"searchfield\").value.split(\" \").length > 0)\n"
                + "                document.querySelector(\".Form\").submit();\n" + "            else\n"
                + "                document.getElementById(\"searchnotes\").innerHTML =\n"
                + "                \"Insufficient Arguments\";\n" + "        }\n" + "    }\n" + "\n"
                + "    function calc() {\n" + "        var str = document.getElementById(\"searchfield\").value;\n"
                + "        var arr = str.split(\", \");\n" + "        if (arr.length < 2)\n"
                + "            document.getElementById(\"searchnotes\").innerHTML =\n"
                + "            \"Insufficient Arguments\";\n" + "        if (arr.length > 2 || arr[1].length > 1)\n"
                + "            document.getElementById(\"searchnotes\").innerHTML = nerdamer\n"
                + "            .solveEquations(arr)\n" + "            .toString();\n" + "        else\n"
                + "            document.getElementById(\"searchnotes\").innerHTML = nerdamer\n"
                + "            .solve(arr[0], arr[1])\n" + "            .toString();\n" + "    }\n" + "</script>\n"
                + "\n" + "</html>";
        return temp;
    }
    public String ResultPage(ArrayList<String> keys, ArrayList<Document> URLS, int size)
    {
        String temp = "  <head>\n" + " <style>\n"
                + "@import url(\"https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap\");\n"
                + "@import url(\"https://fonts.googleapis.com/css2?family=Comfortaa:wght@300;400;500;600;700&display=swap\");\n"
                + "@import url('https://fonts.googleapis.com/css2?family=Source+Code+Pro:wght@200;300;400;500;600;700;900&display=swap');\n"
                + "\n" + "* {\n" + "  margin: 0;\n" + "  padding: 0;\n" + "  box-sizing: border-box;\n" + "}\n"
                + "main {\n" + "  display: flex;\n" + "  flex-direction: row;\n" + "  align-items: flex-start;\n"
                + "  justify-content: stretch;\n" + "  width: 100%;\n" + "  \n" + "}\n" + "input {\n"
                + "  font-family: \"Roboto\";\n" + "  width: 100%;\n" + "  padding: 0.3vw 0.4vw;\n"
                + "  font-size: 1.2vw;\n" + "  background-size: 1.5vw;\n"
                + "  background-image: url(\"https://www.freepngimg.com/download/world_wide_web/62434-engine-web-search-wordpress.com-icons-wallpaper-desktop.png\");\n"
                + "  background-repeat: no-repeat;\n" + "  background-position: 99% center;\n"
                + "  border: solid 2px;\n" + "  border-width: 0 0 2px 0;\n" + "  border-color: #999999;\n"
                + "  margin: 2vw 2vw 2vw 0;\n" + "}\n" + ".Logo{\n" + "    margin: 1vw;\n" + "    width: 5vw;\n" + "}\n"
                + ".ResultCol{\n" + "    display: flex;\n" + "    flex-direction: column;\n" + "    flex: 50% 2 2;\n"
                + "}\n" + ".Stats{\n" + "    flex: 50% 2 2;\n" + "    display: flex;\n"
                + "    justify-content: center;\n" + "    align-items: center;\n" + "    height: 100vh;\n" + "}\n"
                + ".URL { \n" + "    font-family: \"Source Code Pro\";\n" + "    color: #569700;\n"
                + "    font-weight: 600;\n" + "    text-decoration: underline;\n" + "    margin: 0.2vw 0;\n" + "}\n"
                + "\n" + "a{\n" + "    text-decoration: none;\n" + "}\n" + "\n" + ".Title{\n" + "    margin: 0.4vw 0;\n"
                + "    font-size: 1.6vw;\n" + "    color: #006b9d;\n" + "    font-family: \"Roboto\";\n"
                + "    font-weight: 500;\n" + "    text-decoration: none;\n" + "}\n" + "\n"
                + ".Title:hover, .URL:hover{\n" + "    filter: brightness(1.1) saturate(1.4);\n" + "}\n" + "\n" + "\n"
                + "\n" + ".Desc{\n" + "    margin: 0.2vw 0;\n" + "    font-family: 'Comfortaa';\n"
                + "    font-size: 0.9vw;\n" + "    color: #777777;\n" + "    font-weight: 500;\n" + "}\n" + "\n"
                + ".aResult{\n" + "    margin: 1vw;\n" + "}" +"    </style>" + "    <meta charset=\"UTF-8\" />\n"
                + "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n"
                + "    <link rel=\"icon\" href=\"Group_2_1.png\" />\n"
                + "    <link rel=\"stylesheet\" href=\"result.css\" />\n" + "    <title>GoGoGo</title>\n"
                + "  </head>\n" + "  <body>\n" + "    <main>\n" + "      <div class=\"Decor\">\n"
                + "        <a><img class=\"Logo\" src=\"Group_2_1.png\" /></a>\n" + "      </div>\n"
                + "      <div class=\"ResultCol\">\n" + "        <input\n" + "          type=\"text\"\n"
                + "          name=\"searchfield\"\n" + "          id=\"searchfield\"\n" + "          value=\"";
        temp += String.join(" ", keys);
        temp += "\"\n" + "          disabled\n" + "        />";
        // " <% for(i in results.urls) { %>"
        for (org.bson.Document url : URLS) {

            temp += "        <div class=\"aResult\">\n" + "            <a href=\"";
            // "<%= results.urls[i].URL %>" +
            temp += url.getString("URL") + "\"><div class=\"URL\">";
            // "<%= results.urls[i].URL %>" +
            temp += url.getString("URL") + "</div>\n" + "            <div class=\"Title\">";
            // "<%= results.urls[i].Title %>" +
            temp += url.getString("Title") + "</div></a>\n" + "            <div class=\"Desc\">";
            // "<%= results.urls[i].Description %>" +
            temp += url.getString("Description") + "</div>\n" + "        </div>\n";
            // " <% } %>\n" +
        }
        temp += "      </div>\n" + "      <div class=\"Stats\">Number of Results: " + +URLS.size() + "</div>\n"
                + "    </main>\n" + "  </body>";
        return  temp;
    }
    @Autowired
    private final LinkRepo linkRepo;
    public LinkResource(LinkRepo linkRepo) {
        this.linkRepo = linkRepo;
    }
    // REST
    @GetMapping("/") // render this page when the current path is: "/"
    public String goHome() {
        return HomePage();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/findKey") // render this page when the current path is: "/findKey"
    public String getResult(@RequestParam(defaultValue = "") String searchSentence, String mode) {
        System.out.println("I'm searching for: " + searchSentence + " in mode: " + mode);
        // here we split the sentence into words that are used for search process
        String[] searchWords = searchSentence.split(" ");

        ArrayList<String> keys = new ArrayList<>(); // this list should store the list of words we are looking for
        ArrayList<Document> URLS = new ArrayList<>(); // this list should store the list of returned documents from the
                                                      // database,
                                                      // each document contains the URL, Title, Description

        // according to the mode we perform the proper search query query
        if (mode.equals("Or")) {
            // key1-> url1 url5 url3
            // key2-> url5 url3 url7
            // result-> url1 url5 url3 url7
            ArrayList<Document> URLSOrMode = new ArrayList<>();
            for (String word : searchWords) {
                URLSOrMode = linkRepo.findByKey(word).get(0).getURLS();
                URLS.addAll(URLSOrMode);
                keys.add(word);
            }
        } else if (mode.equals("And")) {
            // key1-> url1 url5 url3
            // key2-> url5 url3 url7
            // result-> url5 url3
            HashMap<Document, Long> URLSAndMode = new HashMap<>();
            for (String word : searchWords) {
                ArrayList<Document> queryResult = linkRepo.findByKey(word).get(0).getURLS();

                for (int i = 0; i < queryResult.size(); i++) {
                    if (URLSAndMode.containsKey(queryResult.get(i))) {
                        URLSAndMode.put(queryResult.get(i), URLSAndMode.get(queryResult.get(i)) + 1);
                        URLS.add(queryResult.get(i));
                    } else {
                        URLSAndMode.put(queryResult.get(i), 1L);
                    }
                }
            }
        }
        return ResultPage(keys, URLS, keys.size());
    }
}
