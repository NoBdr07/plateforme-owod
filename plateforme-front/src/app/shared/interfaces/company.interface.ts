export interface Company {
  id: string;
  name: string;

  description?: string | null;
  email?: string | null;
  raisonSociale?: string | null;
  siretNumber?: string | null;
  phoneNumber?: string | null;
  sectors?: string[] | null;
  stage?: string | null;
  type?: string | null;
  country?: string | null;
  city?: string | null;
  revenue?: string | null;
  logoUrl?: string | null;
  websiteUrl?: string | null;

  teamPhotoUrl?: string | null;
  worksUrl?: string[] | null;
  employeesId?: string[] | null;

  financialSupport?: boolean | false;
}