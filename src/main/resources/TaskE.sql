SELECT S."monthDate"     as "monthDate",
       S."description"   as "description",
       S."name"          as "item_name",
       S."category_name" as "category_name",
       S."brand_name"    as "brand_name",
       S."sum_shows"     as "sum_shows",
       S."sum_clicks"    as "sum_clicks",
       S."sum_orders"    as "sum_orders",
       S."sum_gmv"       as "sum_gmv"
FROM (
         SELECT S.monthDate,
                B.name        as brand_name,
                C.name        as category_name,
                S.name,
                S.description,
                sum(S.shows)  as sum_shows,
                sum(S.clicks) as sum_clicks,
                sum(S.orders) as sum_orders,
                sum(S.gmv)    as sum_gmv
         FROM (
                  SELECT date(s.date, 'start of month') as monthDate,
                         S.brand_id,
                         S.category_id,
                         S.name,
                         S.description,
                         S.shows,
                         S.clicks,
                         S.orders,
                         S.gmv
                  FROM statistic AS S
                           LEFT JOIN
                       (
                           SELECT B.id
                           FROM brands AS B
                           WHERE B.name in ('Apple', 'Samsung')
                       ) EB ON EB.id = S.brand_id
                           LEFT JOIN
                       (
                           SELECT C.id
                           FROM categories AS C
                           WHERE C.name in ('Варочные панели', 'Садовые измельчители')
                       ) EC ON EC.id = S.category_id
                  WHERE S.date >= '2021-04-01'
                    AND S.date <= '2021-07-31'
                    AND NOT (S.shows < S.clicks)
                    AND NOT (S.clicks < S.orders)
                    AND EB.id is null
                    AND EC.id is null
              ) S
                  LEFT JOIN brands B On B.id = S.brand_id
                  LEFT JOIN categories C ON C.id = S.category_id
         group by S.description,
                  S.name,
                  S.monthDate,
                  B.name,
                  C.name
     ) S
ORDER BY "monthDate",
         "sum_gmv" desc
LIMIT 100;