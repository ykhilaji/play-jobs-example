package model
import scala.util.Try
import scala.util.matching.Regex
import java.time.LocalDate

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import enum.Enum
import play.api.libs.json._
import play.api.libs.functional.syntax._


case class AddressModel(
  reference: Option[String],
  company: Option[String],
  service: Option[String],
  name: Option[String],
  firstname: Option[String],
  phone: Option[String],
  cellular: Option[String],
  email: Option[String],
  address1: Option[String],
  address2: Option[String],
  address3: Option[String],
  address4: Option[String],
  zipcode: Option[String],
  city: String,
  countryIso: String,
  provinceOuEtatName : Option[String],
  stateOrProvinceCode : Option[String],
  doorcode1: Option[String],
  doorcode2: Option[String],
  intercom: Option[String],
  isPro: Option[Boolean] = Some(false),
  promotionCode: Option[String],
  infocomp: Option[String],
  commercialName: Option[String]
)

object AddressModel {

  import ai.x.play.json.Jsonx
  implicit val addressFormat : Format[AddressModel] = Jsonx.formatCaseClass[AddressModel]

}

case class Produit(code: String, label: String,  flag : Option[Boolean] = Some(false) )

object Produit {
  implicit val format: Format[Produit] = Json.format[Produit]
}



case class PointRetrait(
  identifiant: String,
  typeDePoint: Option[String] = None,
  company: Option[String] = None,
  address1: Option[String] = None,
  address2: Option[String] = None,
  address3: Option[String] = None,
  address4: Option[String] = None,
  countryIso: Option[String] = None,
  zipcode: Option[String] = None,
  city: Option[String] = None,
  accountNumber: Option[String] = None,
  isManual: Option[Boolean] = Some(false)
)

object PointRetrait {
  implicit val pointRetraitFormat = Json.format[PointRetrait]
}


case class Options(
  avis_reception: Option[Boolean],
  contre_remboursement: Option[String],
  recommandation: Option[String],
  valeur_assuree: Option[String],
  langue_notification: Option[String],
  delivery_duty_paid: Option[Boolean],
  franc_taxes_droits: Option[Boolean]
)

object Options {
  implicit val format: Format[Options] = Json.format[Options]
}

case class ArticleModel(reference: Option[String],
                    description: Option[String],
                    countryIso: Option[String],
                    quantite: Option[Int],
                    poids: Double,
                    valeur: Option[Double],
                    numtarifaire: Option[String],
                    devise: Option[String],
                    idOrigine: Option[String], // key to link InfosOrigine with an Article
                    droitDouane: Option[Double],
                    tva: Option[Double],
                    colisType: Option[String],
                    etiquetteId: Option[String]
                  )

object ArticleModel  {

  implicit val format: Format[ArticleModel] = Json.format[ArticleModel]

}


sealed trait InstructionNonLivraison
import core.JsonImplicits

object InstructionNonLivraison {
  case object RienFaire extends InstructionNonLivraison
  case object Retourner extends InstructionNonLivraison

  implicit val enum: Enum[InstructionNonLivraison] = Enum.derived[InstructionNonLivraison]
  implicit val format: Format[InstructionNonLivraison] = JsonImplicits.enumFormat(enum)

  def toInt(info: InstructionNonLivraison): Int =
    info match {
      case RienFaire => 3
      case Retourner => 2
    }

  def fromInt(i: Int): Option[InstructionNonLivraison] =
    enum.values.toSeq.find(toInt(_) == i)
}

case class InfosOrigine(
  id: String, // key to link InfosOrigine with an Article
  factureOrigine: String,
  factureDate: String,
  numColis: String
)

object InfosOrigine {
  implicit val format = Json.format[InfosOrigine]

}



case class CaracteristiquesModel(
  poidskg: String,
  refcommande: Option[String],
  datedepot: Option[String] = None,
  genererRetour: Boolean,
  signature: Option[Boolean],
  engagementDelai: Option[Boolean],
  economique : Option[Boolean] = Some(false),
  nonMachinable: Boolean,
  options: Option[Options],
  isCN23: Boolean,
  genererCN23: Boolean,
  hauteur: Option[String],
  largeur: Option[String],
  longueur: Option[String],
  envoi: Option[String],
  certificat: Option[String],
  facture: Option[String],
  licence: Option[String],
  observations: Option[String],
  articles: Option[Seq[ArticleModel]],
  retourOption: Option[InstructionNonLivraison],
  formatColis: Option[String],
  poidsVolumetrique: Option[Double] = None,
  infosOrigines: Option[Seq[InfosOrigine]] = None,
  contreRemboursement: Option[Double] = None,
  enteteLigneColis: Option[String],
  explications: Option[String],
  importateur : Option[Importateur],
  fraisDePort: Option[String],
  invoiceNumber : Option[String],
  referenceDouane : Option[String],
  numEori : Option[String],
  tagUsers : Option[String],
  isDDP : Option[Boolean] = Some(false),
  descriptionGen : Option[String],
  groupage: Option[Groupage]
)



case class Groupage (
  isColisSuiveur: Boolean,
  number: Option[Int],
  totalNumber: Option[Int]
)

object Groupage {
  implicit val format = Json.format[Groupage]
}


object CaracteristiquesModel {
  import ai.x.play.json.Jsonx
  implicit val format: Format[CaracteristiquesModel] = Jsonx.formatCaseClass[CaracteristiquesModel]
}


case class MontantDesOptions(tarifDesOptions: Seq[TarifOption]) {
  def findMontantOption(codeOption: String): Option[Double] = tarifDesOptions.find(_.option.codeOption == codeOption).flatMap(_.montantHT)
}
object MontantDesOptions {
  implicit val format: Format[MontantDesOptions] = Json.format[MontantDesOptions]
}

case class TarifOption(option: OptionInfo, montantHT: Option[Double])
object TarifOption {
  implicit val format: Format[TarifOption] = Json.format[TarifOption]
}

case class OptionInfo(codeOption: String)
object OptionInfo {
  implicit val format: Format[OptionInfo] = Json.format[OptionInfo]
}


case class Importateur(
  company: Option[String] = None,
  reference: Option[String] = None,
  name: Option[String] = None,
  firstname: Option[String] = None,
  address1: Option[String] = None,
  address2: Option[String] = None,
  address3: Option[String] = None,
  address4: Option[String] = None,
  countryIso: Option[String] = None,
  zipcode: Option[String] = None,
  city: Option[String] = None,
  email: Option[String] = None,
  phone: Option[String] = None,
  cellular: Option[String] = None
)

object Importateur {
  implicit val format: Format[Importateur] = Json.format[Importateur]
}


case class Field(key: String, value: String)

object Field {
  implicit val format = Json.format[Field]
  val lpServiceWrites = new Writes[Field] {
    def writes(field: Field) =
      Json.obj("key" -> field.key, "value" -> field.value)
  }
}

sealed trait EtiquetteModel

/**
object EtiquetteModel {

   implicit object Writes extends Writes[EtiquetteModel] {
    override def writes(o: EtiquetteModel): JsValue = {
      o match {
        case i: EtiquetteModelAdapter => EtiquetteModelAdapter.format.writes(i)
        case x => sys.error("type not found " + o)
      }
    }
  }

   implicit object Reads extends Reads[EtiquetteModel] {
    override def reads(json: JsValue): JsResult[EtiquetteModel] = {
      val msgType = (json \ "id").as[Long]
      msgType match {
        case value => EtiquetteModelAdapter.format.reads(json)
      }
    }
  }
}
*/

case class EtiquetteModelAdapter(
                      expediteur: Option[AddressModel],
                      destinataire: Option[AddressModel],
                      prix: Option[Double],
                      prixRetour: Option[Double],
                      prixOptions: Option[Double],
                      produit: Option[Produit],
                      pointRetrait: Option[PointRetrait],
                      caracteristiques: Option[CaracteristiquesModel],
                      montantDesOptions: Option[MontantDesOptions],
                      availableOptions: Option[Seq[JsValue]],
                      availableProducts: Option[Seq[JsValue]],
                      retour: Option[Boolean],
                      collecte : Option[Boolean] = Some(false),
                      deleted : Option[Boolean],
                      numeroCommande: Option[String],
                      customFields: Option[Seq[Field]],
                      field: Option[Seq[Field]],
                      colisId: Option[String] = None,
                      crbt: Option[AddressModel] = None,
                      cn23: Option[AddressModel] = None,
                      partenaireCab: Option[String] = None,
                      partenaireNom: Option[String] = None,
                      numeroDepot: Option[Int] = None,
                      lineNumer : Option[Int] = None,
                      pluginActive : Option[Boolean] = Some(true),
                      isReturnByLot : Option[Boolean] = Some(false),
                      command : Option[Boolean] = Some(false),
                      cmdId: Option[Long]= None

) extends EtiquetteModel

object EtiquetteModelAdapter{

  implicit val customFieldFormat = Field.format
  import ai.x.play.json.Jsonx
  implicit val format: Format[EtiquetteModelAdapter] = Jsonx.formatCaseClass[EtiquetteModelAdapter]
}
