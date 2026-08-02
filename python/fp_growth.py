import os
import sys
import json
import warnings

warnings.filterwarnings("ignore")
os.environ['PYTHONWARNINGS'] = 'ignore'

import pymysql
import pandas as pd
from mlxtend.preprocessing import TransactionEncoder
from mlxtend.frequent_patterns import fpgrowth, association_rules

def get_data_from_db():
    try:
        conn = pymysql.connect(
            host="localhost",
            user="root",
            password="",
            database="db_warung_haryati"
        )
        query = """
            SELECT t.transaksi_id as transaction_id, p.nama_barang 
            FROM detail_transaksi dt 
            JOIN transaksi t ON dt.transaksi_id = t.transaksi_id 
            JOIN produk p ON dt.produk_id = p.id_produk
        """
        df = pd.read_sql(query, conn)
        conn.close()
        return df
    except Exception as e:
        print(f"Python Error: {str(e)}", file=sys.stderr)
        print(json.dumps({"error": str(e)}))
        sys.exit(1)


def run_fp_growth(min_support, min_confidence):
    df = get_data_from_db()
    
    if df.empty:
        print(json.dumps({"frequent_itemsets": [], "association_rules": []}))
        return

    transactions = df.groupby('transaction_id')['nama_barang'].apply(list).values.tolist()
    
    te = TransactionEncoder()
    te_ary = te.fit(transactions).transform(transactions)
    df_encoded = pd.DataFrame(te_ary, columns=te.columns_)
    
    frequent_itemsets = fpgrowth(df_encoded, min_support=min_support, use_colnames=True)
    
    if frequent_itemsets.empty:
        print(json.dumps({"frequent_itemsets": [], "association_rules": []}))
        return

    rules = association_rules(frequent_itemsets, metric="confidence", min_threshold=min_confidence)
    
    if not rules.empty:
        rules = rules[rules['lift'] > 1.0]
    
    frequent_itemsets['itemsets'] = frequent_itemsets['itemsets'].apply(list)
    
    rules_list = []
    for _, row in rules.iterrows():
        rules_list.append({
            "antecedents": list(row['antecedents']),
            "consequents": list(row['consequents']),
            "support": float(row['support']),
            "confidence": float(row['confidence']),
            "lift": float(row['lift'])
        })
            
    result = {
        "frequent_itemsets": frequent_itemsets.to_dict(orient='records'),
        "association_rules": rules_list
    }
    
    print(json.dumps(result))

if __name__ == "__main__":
    min_sup = 0.02
    min_conf = 0.6
    
    if len(sys.argv) > 1:
        try:
            min_sup = float(sys.argv[1])
        except ValueError: pass
    if len(sys.argv) > 2:
        try:
            min_conf = float(sys.argv[2])
        except ValueError: pass
        
    run_fp_growth(min_sup, min_conf)
