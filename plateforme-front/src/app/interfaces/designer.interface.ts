export interface Designer {
    id: string; // Identifiant unique
  
    email: string; // Adresse email
    profilePicture: string; // URL de la photo de profil
    firstName: string; // Prénom
    lastName: string; // Nom
    biography: string; // Biographie
    phoneNumber: string; // Numéro de téléphone
    profession: string; // Profession
    specialties: string[]; // Liste des spécialités
    spheresOfInfluence: string[]; // Liste des sphères d'influence
    favoriteSectors: string[]; // Liste des secteurs favoris
    countryOfOrigin: string; // Pays d'origine
    countryOfResidence: string; // Pays de résidence
    professionalLevel: string; // Niveau professionnel (junior, senior, expert)
    majorWorks: string[]; // URL des photos des réalisations majeures (<= 5)
    portfolioUrl: string; // URL du portfolio
  }
  