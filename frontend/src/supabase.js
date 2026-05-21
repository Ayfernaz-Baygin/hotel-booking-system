import { createClient } from "@supabase/supabase-js";

const supabaseUrl = "https://djfpopgagpbxddytasrm.supabase.co";

const supabaseAnonKey = "sb_publishable_SMqAZXDWRGKXlEV6aZmv8g_VpFJ36mV";

export const supabase = createClient(
  supabaseUrl,
  supabaseAnonKey
);