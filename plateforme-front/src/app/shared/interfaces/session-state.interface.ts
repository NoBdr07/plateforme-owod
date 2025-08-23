import { AccountType } from "../enums/account-type.enum";
import { User } from "./user.interface";

export interface SessionState {
  isLogged: boolean;
  isAdmin: boolean;
  userId: string | null;
  firstname: string | null;
  lastname: string | null;
  accountType: AccountType;   // NONE | DESIGNER | COMPANY
  designerId?: string | null;
  companyId?: string | null;
}