import { AccountType } from "../enums/account-type.enum";

export interface SessionInfo {
  userId: string;
  firstname: string;
  lastname: string;
  roles: string[];
  accountType: AccountType;     // correspond à ton enum front (mêmes labels)
  designerId: string | null;
  companyId: string | null;
}