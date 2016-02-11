package org.dbpedia.extraction.mappings

import org.dbpedia.extraction.destinations.{DBpediaDatasets, Quad, QuadBuilder}
import org.dbpedia.extraction.ontology.Ontology
import org.dbpedia.extraction.util.{ExtractorUtils, Language}
import org.dbpedia.extraction.wikiparser.{Namespace, _}

import scala.language.reflectiveCalls

/**
 * Extracts redirect links between Articles in Wikipedia.
 */
class RedirectNodeExtractor (
                          context : {
                            def ontology : Ontology
                            def language : Language
                          }
                          )
  extends PageNodeExtractor
{
  private val language = context.language

  private val wikiPageRedirectsProperty = context.ontology.properties("wikiPageRedirects")

  override val datasets = Set(DBpediaDatasets.Redirects)

  private val namespaces = if (language == Language.Commons) ExtractorUtils.commonsNamespacesContainingMetadata
  else Set(Namespace.Main, Namespace.Template, Namespace.Category)

  private val quad = QuadBuilder(language, DBpediaDatasets.Redirects, wikiPageRedirectsProperty, null) _

  override def extract(page : PageNode, subjectUri : String, pageContext : PageContext): Seq[Quad] =
  {
     if (page.isRedirect) {
      for(link <- ExtractorUtils.collectInternalLinksFromNode(page)) {
        return Seq(quad(subjectUri, language.resourceUri.append(link.destination.decodedWithNamespace), link.sourceUri))
      }
    }

    Seq.empty
  }
}
