import config.DocumentLanguage
import entity.*

package object i18n {

  object I18N {

    object NavBar {
      val TITLE        = "nav-title"
      val HOME         = "nav-home"
      val CREATE_POLL  = "nav-create-poll"
      val ANSWER_POLL  = "nav-answer-poll"
      val VIEW_RESULTS = "nav-view-results"
    }

    object Home {
      val DEFINITION = "home-top-paragraph"
      val IDEA_1     = "home-idea-1"
      val IDEA_2     = "home-idea-2"
    }

    object AnswerPoll {
      val MISSING_CODE = "answer-poll-missing-code"
      val TITLE        = "answer-poll-poll-title"
      val RESULTS_HERE = "answer-poll-results-here"
      val ANSWER_BTN   = "answer-poll-button"
      val ANSWERED     = "answer-poll-answered"
      val HERE         = "answer-poll-here"
    }

    object ViewResults {
      val RESPONSE = "view-results-response"
      val VOTES    = "view-results-votes"
      val AVERAGE  = "view-results-average"
    }

    object Footer {
      val CODE = "footer-code"
    }
  }

  val languageMaps: Map[DocumentLanguage, Map[String, String]] = Map(
    DocumentLanguage.en -> Map(
      // Navbar
      I18N.NavBar.TITLE        -> "Anonymous Poll \uD83C\uDFAD",
      I18N.NavBar.HOME         -> "Home",
      I18N.NavBar.CREATE_POLL  -> "Create Poll",
      I18N.NavBar.ANSWER_POLL  -> "Answer Poll",
      I18N.NavBar.VIEW_RESULTS -> "View Results",

      // Home Page
      I18N.Home.DEFINITION -> "This is an anonymous poll app, which implies the following:",
      I18N.Home.IDEA_1     -> "For any poll, you can only vote once using the email received link",
      I18N.Home.IDEA_2     -> "The poll is deleted two days after its creation OR on server restart",

      // Answer Poll Page
      I18N.AnswerPoll.MISSING_CODE -> "⚠️ You are missing the vote code. Unable to perform poll retrieval",
      I18N.AnswerPoll.TITLE        -> """"%s" poll""",
      I18N.AnswerPoll.RESULTS_HERE -> "Results can be viewed ",
      I18N.AnswerPoll.ANSWER_BTN   -> "Register answers",
      I18N.AnswerPoll.ANSWERED     -> "Thank you for your answers! Results can be viewed ",
      I18N.AnswerPoll.HERE         -> "here",

      // View Results Page
      I18N.ViewResults.RESPONSE -> "Response",
      I18N.ViewResults.VOTES    -> "Votes",
      I18N.ViewResults.AVERAGE  -> "Average",

      // Footer
      I18N.Footer.CODE -> "\uD83D\uDC49\u00A0\u00A0Code on GitHub"
    ),
    DocumentLanguage.ro -> Map(
      // Navbar
      I18N.NavBar.TITLE        -> "Sondaj Anonim \uD83C\uDFAD",
      I18N.NavBar.HOME         -> "Acasă",
      I18N.NavBar.CREATE_POLL  -> "Creează Sondaj",
      I18N.NavBar.ANSWER_POLL  -> "Completează Sondaj",
      I18N.NavBar.VIEW_RESULTS -> "Vezi Rezultate",

      // Home Page
      I18N.Home.DEFINITION -> "Aceasta este o aplicație de sondaje anonime, ceea ce implică:",
      I18N.Home.IDEA_1     -> "Pentru orice sondaj, se poate vota o singură dată cu linkul primit pe email",
      I18N.Home.IDEA_2     -> "Sondajul se sterge automat după 2 zile sau când este repornit serverul",

      // Answer Poll Page
      I18N.AnswerPoll.MISSING_CODE -> "⚠️ Îți lipsește codul de votare. Nu se poate obține sondajul.",
      I18N.AnswerPoll.TITLE        -> """Sondaj "%s"""",
      I18N.AnswerPoll.RESULTS_HERE -> "Rezultatele pot fi văzute ",
      I18N.AnswerPoll.ANSWER_BTN   -> "Înregistrează răspunsuri",
      I18N.AnswerPoll.ANSWERED     -> "Mulțumim pentru răspunsuri! Rezultatele pot fi văzute ",
      I18N.AnswerPoll.HERE         -> "aici",

      // View Results Page
      I18N.ViewResults.RESPONSE -> "Răspunsuri",
      I18N.ViewResults.VOTES    -> "Voturi",
      I18N.ViewResults.AVERAGE  -> "În medie",

      // Footer
      I18N.Footer.CODE -> "\uD83D\uDC49\u00A0\u00A0Codul pe GitHub"
    )
  )
}
