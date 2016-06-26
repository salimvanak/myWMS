--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

ALTER TABLE ONLY public.los_sulabel DROP CONSTRAINT fk_x9k9rrv8hw9o8olpdkskfsbf;
ALTER TABLE ONLY public.los_ul_record DROP CONSTRAINT fk_tkjcu9jv0ijq85hy1xeftrexg;
ALTER TABLE ONLY public.los_storagereq DROP CONSTRAINT fk_tjdx5g7umgn60qx8fvpfa3nag;
ALTER TABLE ONLY public.los_goodsreceipt DROP CONSTRAINT fk_t9xgvfmd63so9j7o3ynurxgtj;
ALTER TABLE ONLY public.mywms_unitload DROP CONSTRAINT fk_t0lx7lasdejvdbcri6eyc6acl;
ALTER TABLE ONLY public.mywms_lot DROP CONSTRAINT fk_s82luwfngpx8rdidm0u300nca;
ALTER TABLE ONLY public.mywms_stockunit DROP CONSTRAINT fk_s72np1wb34rw5ui65rf74yq08;
ALTER TABLE ONLY public.los_orderreceiptpos DROP CONSTRAINT fk_s341pty5ctuw95akotxewfvig;
ALTER TABLE ONLY public.los_customerorder DROP CONSTRAINT fk_s2hdwgcthb3c2tm0a37w0so6a;
ALTER TABLE ONLY public.mywms_stockunit DROP CONSTRAINT fk_rx6tor6md073bcuiibc81kc82;
ALTER TABLE ONLY public.mywms_stockunit DROP CONSTRAINT fk_rssjymt81gwmy3dnvbfx4ys1l;
ALTER TABLE ONLY public.los_orderstrat DROP CONSTRAINT fk_rqvhfrr8or1k0m9cwm5idvjpb;
ALTER TABLE ONLY public.los_pickingorder DROP CONSTRAINT fk_rmo4pkod81eqao9dp69bnxhmc;
ALTER TABLE ONLY public.los_fixassgn DROP CONSTRAINT fk_ragoiuxtml9fkukvfgf4m6x0v;
ALTER TABLE ONLY public.los_replenishorder DROP CONSTRAINT fk_qplxtuq5bk6d24h1m01xfpdy8;
ALTER TABLE ONLY public.los_pickingpos DROP CONSTRAINT fk_qg8cupcbjj9efvo9uab28o6ks;
ALTER TABLE ONLY public.los_pickingorder DROP CONSTRAINT fk_q42vcywt7qfulojmcyr43iivc;
ALTER TABLE ONLY public.los_storagereq DROP CONSTRAINT fk_pvnvku8h5vpw2fhbbii2tcpji;
ALTER TABLE ONLY public.los_outreq DROP CONSTRAINT fk_pun85eb2sqopmxvggdc3adfhq;
ALTER TABLE ONLY public.los_rack DROP CONSTRAINT fk_ptkjaig5j9at5brfjlpo1av2j;
ALTER TABLE ONLY public.los_goodsreceipt_los_avisreq DROP CONSTRAINT fk_pfhkpqs7kd2gajhw2m1ixee2j;
ALTER TABLE ONLY public.los_itemdata_number DROP CONSTRAINT fk_p58yyogfogmidi5oodybjfpeu;
ALTER TABLE ONLY public.mywms_zone DROP CONSTRAINT fk_ors6m1tghquna7ndlb7wur0io;
ALTER TABLE ONLY public.los_sllabel DROP CONSTRAINT fk_oggftxiqb67aj5iesdievo042;
ALTER TABLE ONLY public.mywms_user_mywms_role DROP CONSTRAINT fk_oenr4tgr11ij76j0g9y3sypxg;
ALTER TABLE ONLY public.mywms_document DROP CONSTRAINT fk_o9vq0nm3ccwx1p7e3u8u4q6cj;
ALTER TABLE ONLY public.mywms_area DROP CONSTRAINT fk_o8ukym12is74hg3pyocb0yc70;
ALTER TABLE ONLY public.los_outreq DROP CONSTRAINT fk_o4602pdlixeb598sh829lvd9h;
ALTER TABLE ONLY public.los_outpos DROP CONSTRAINT fk_na80f6x7re3py1dxy86iu79ed;
ALTER TABLE ONLY public.mywms_clearingitem DROP CONSTRAINT fk_mxi0q9s0yv4qewo54ejthtr0u;
ALTER TABLE ONLY public.los_storloc DROP CONSTRAINT fk_mrgibufynmr5ht5ci3pl6pggs;
ALTER TABLE ONLY public.los_pickingpos DROP CONSTRAINT fk_mc5hm24i682cb3wdhhuijnvfl;
ALTER TABLE ONLY public.los_goodsreceipt_los_avisreq DROP CONSTRAINT fk_m9trernv5qlwfhfo0r3mra8cj;
ALTER TABLE ONLY public.los_pickingpos DROP CONSTRAINT fk_ltdnny9x5afypoudst3xn0ki5;
ALTER TABLE ONLY public.los_stocktakingrecord DROP CONSTRAINT fk_ld9jgvv5vsbl3ivq828e9n6m7;
ALTER TABLE ONLY public.los_workingareapos DROP CONSTRAINT fk_l7va4kr8pqp7fei9w3pq9ryu1;
ALTER TABLE ONLY public.los_storloc DROP CONSTRAINT fk_kufr8yv66188js6gtuo1lq1ti;
ALTER TABLE ONLY public.los_grrposition DROP CONSTRAINT fk_kmnpsoj1cpequhlcstnmiggrp;
ALTER TABLE ONLY public.los_grrposition DROP CONSTRAINT fk_klo6kt8khoi1p3000obnw9yyu;
ALTER TABLE ONLY public.mywms_unitload DROP CONSTRAINT fk_kk2nsc552ktp91sx1u0v91ofe;
ALTER TABLE ONLY public.los_outreq DROP CONSTRAINT fk_kfgfdk9fxu88u9fano6omagy4;
ALTER TABLE ONLY public.los_pickingpos DROP CONSTRAINT fk_kdvp08l6s8rkccyroqqpyjfxv;
ALTER TABLE ONLY public.mywms_user_mywms_role DROP CONSTRAINT fk_kaciu0belofcq7dbq2r7b1oq0;
ALTER TABLE ONLY public.los_outpos DROP CONSTRAINT fk_jsid3q73fchdor7xoqyfaiqvx;
ALTER TABLE ONLY public.los_pickingpos DROP CONSTRAINT fk_jpnw26uxyeq2atbqp9m9wo8jn;
ALTER TABLE ONLY public.los_avisreq DROP CONSTRAINT fk_jmeh8r6wh3tox725bfqx7l7c2;
ALTER TABLE ONLY public.los_goodsreceipt DROP CONSTRAINT fk_j5ibey76d6m0tv9antwbxb2c0;
ALTER TABLE ONLY public.los_uladvicepos DROP CONSTRAINT fk_j44d37d213iecfbv9odra8g67;
ALTER TABLE ONLY public.los_customerpos DROP CONSTRAINT fk_igt8tbkvpvcc3m2mx6ng3umea;
ALTER TABLE ONLY public.los_storagestrat DROP CONSTRAINT fk_hppd4sdp7kli4nefqlxu9re73;
ALTER TABLE ONLY public.los_avisreq DROP CONSTRAINT fk_ho705f7xm4vxrgxdy8i3dsw25;
ALTER TABLE ONLY public.los_pickingunitload DROP CONSTRAINT fk_hguvp4j4w6a4wb29hbk5kjjeo;
ALTER TABLE ONLY public.los_customerpos DROP CONSTRAINT fk_h37peta7gs3hq4jan4ppyu6r7;
ALTER TABLE ONLY public.los_replenishorder DROP CONSTRAINT fk_gp13erauhm4jy7iiaf2q4maew;
ALTER TABLE ONLY public.los_replenishorder DROP CONSTRAINT fk_gl1i1heemgxcp9n5xm5qx17jw;
ALTER TABLE ONLY public.mywms_user DROP CONSTRAINT fk_g7no3mxnrsxols29wm5d10cr6;
ALTER TABLE ONLY public.los_grrposition DROP CONSTRAINT fk_g64scbxylnibnwgcq7y8oqbr6;
ALTER TABLE ONLY public.los_customerpos DROP CONSTRAINT fk_fu9rlp10ofosts62rn37lpuc7;
ALTER TABLE ONLY public.los_storloc DROP CONSTRAINT fk_f6gvv7xh5jr23pvukq17va4w8;
ALTER TABLE ONLY public.los_orderreceiptpos DROP CONSTRAINT fk_eyuhlds1idjue9bt3ic12j6vi;
ALTER TABLE ONLY public.los_grrposition DROP CONSTRAINT fk_ehdfbhdxxli6f8k7gagqgm1nu;
ALTER TABLE ONLY public.los_bom DROP CONSTRAINT fk_edxhx7qmcxv8t8dl1pi9pd7qd;
ALTER TABLE ONLY public.los_uladvice DROP CONSTRAINT fk_duhl69nyvlqseyg5jn7ob5pbs;
ALTER TABLE ONLY public.mywms_itemdata DROP CONSTRAINT fk_du57jxrmnskys7fhsm9ifxkig;
ALTER TABLE ONLY public.los_itemdata_number DROP CONSTRAINT fk_dkijwsa9nwom5yy1y84ftk3nn;
ALTER TABLE ONLY public.los_pickingpos DROP CONSTRAINT fk_dka6b9a9ma82xfbxs1wcfgvhr;
ALTER TABLE ONLY public.los_typecapacityconstraint DROP CONSTRAINT fk_dg5kvwoefx6xb5y6qw07c4u13;
ALTER TABLE ONLY public.los_uladvicepos DROP CONSTRAINT fk_d0me94ors6djvprbgtaw61uco;
ALTER TABLE ONLY public.mywms_stockunit DROP CONSTRAINT fk_crewwou4hu6pclml7qc3r224m;
ALTER TABLE ONLY public.los_customerorder DROP CONSTRAINT fk_c8r1disprpf8pa0kx9g51cmth;
ALTER TABLE ONLY public.los_grrposition DROP CONSTRAINT fk_bur1f0gldpj7a4vtg3jvfdhbe;
ALTER TABLE ONLY public.los_outreq DROP CONSTRAINT fk_bboid4k8cgaya4p9gx4ppbjhj;
ALTER TABLE ONLY public.los_goodsreceipt DROP CONSTRAINT fk_b86m7r1t910xoum3hw9s6vyl2;
ALTER TABLE ONLY public.mywms_itemdata DROP CONSTRAINT fk_b545jd5xoe053wey53jorwsqt;
ALTER TABLE ONLY public.los_storloc DROP CONSTRAINT fk_awcs6iuc0axq7vkfjdqaa0ppu;
ALTER TABLE ONLY public.los_orderreceipt DROP CONSTRAINT fk_avqboyarw7gnxl757cry48slb;
ALTER TABLE ONLY public.los_orderstrat DROP CONSTRAINT fk_amswid6we3ym57fojfuqwcu45;
ALTER TABLE ONLY public.los_storloc DROP CONSTRAINT fk_alggbthaao9shflps5x86grgm;
ALTER TABLE ONLY public.los_customerpos DROP CONSTRAINT fk_ajch7flpoc7qs3uc6ah43ed62;
ALTER TABLE ONLY public.los_serviceconf DROP CONSTRAINT fk_ac9oo03pkmumii68a95afuumj;
ALTER TABLE ONLY public.los_sysprop DROP CONSTRAINT fk_a6jq257y1sy7hds4wu5kr4vnm;
ALTER TABLE ONLY public.los_uladvice DROP CONSTRAINT fk_9yvl792jj096ypw47e5868evw;
ALTER TABLE ONLY public.mywms_itemunit DROP CONSTRAINT fk_9wcovwxiehc3m6qmourf51yoj;
ALTER TABLE ONLY public.mywms_pluginconfiguration DROP CONSTRAINT fk_9w8pqnnlu1qps727bcfvedt2a;
ALTER TABLE ONLY public.los_replenishorder DROP CONSTRAINT fk_97cf20tqdcnkmbm7fa39aqt4y;
ALTER TABLE ONLY public.los_typecapacityconstraint DROP CONSTRAINT fk_97109qj5x304yfy7bu7bvhbh2;
ALTER TABLE ONLY public.los_customerorder DROP CONSTRAINT fk_93sv79j1bbe2cqkswwoe3sryy;
ALTER TABLE ONLY public.mywms_lot DROP CONSTRAINT fk_922n1k9u65oev39aw870j8bn5;
ALTER TABLE ONLY public.los_uladvicepos DROP CONSTRAINT fk_8by4srayjumajd85uk9erxeec;
ALTER TABLE ONLY public.los_storloc DROP CONSTRAINT fk_85ffoaf3o9m7flxq324f46xo1;
ALTER TABLE ONLY public.mywms_logitem DROP CONSTRAINT fk_7sgb82flevf1vwt4rwsnwqjjh;
ALTER TABLE ONLY public.los_pickingpos DROP CONSTRAINT fk_7lo99vpft3x9a5a4139wl9isv;
ALTER TABLE ONLY public.los_storagereq DROP CONSTRAINT fk_7g670uvl24xusdtcs83gy7yhr;
ALTER TABLE ONLY public.los_stocktakingorder DROP CONSTRAINT fk_7ex4ci2v36v1yer7dvvvccdyj;
ALTER TABLE ONLY public.los_pickingorder DROP CONSTRAINT fk_7bcrglw1jqm6ryg4i9wx4w2an;
ALTER TABLE ONLY public.los_stockrecord DROP CONSTRAINT fk_6ve1bn3rex56rglr8xeterc4d;
ALTER TABLE ONLY public.los_storloc DROP CONSTRAINT fk_6rmt63k21bc45m2va3re1a86v;
ALTER TABLE ONLY public.los_bom DROP CONSTRAINT fk_6kssimx3jloe6lh419r7gfy18;
ALTER TABLE ONLY public.los_pickingunitload DROP CONSTRAINT fk_6iu26onngx8e6a4fr1qhswdiq;
ALTER TABLE ONLY public.los_uladvicepos DROP CONSTRAINT fk_68kqbao07jore36o3e83pvdac;
ALTER TABLE ONLY public.los_pickingorder DROP CONSTRAINT fk_637vvkl8tp58mmqgqj3mdermo;
ALTER TABLE ONLY public.los_jasperreport DROP CONSTRAINT fk_5wovrjjy9ocjcgqfksdmmkodd;
ALTER TABLE ONLY public.los_replenishorder DROP CONSTRAINT fk_5vq9h4yubp1e04423ifip3ppv;
ALTER TABLE ONLY public.mywms_request DROP CONSTRAINT fk_5pq174elkqu0875yor3bnprtt;
ALTER TABLE ONLY public.los_workingareapos DROP CONSTRAINT fk_54fk6llpmvin3avdf0uifvxwr;
ALTER TABLE ONLY public.los_pickreceipt DROP CONSTRAINT fk_53f7ppwcwe4h0yylvrfelux7w;
ALTER TABLE ONLY public.los_pickingpos DROP CONSTRAINT fk_51n126rr6vt4huvr1j8fyq549;
ALTER TABLE ONLY public.los_replenishorder DROP CONSTRAINT fk_4kf680cdagiqc3iq5p9l0kfp4;
ALTER TABLE ONLY public.mywms_unitload DROP CONSTRAINT fk_4hga11ayl6r72egfb4xl3c20r;
ALTER TABLE ONLY public.los_fixassgn DROP CONSTRAINT fk_3yp785ptpbjt85294dvimodx8;
ALTER TABLE ONLY public.mywms_itemdata DROP CONSTRAINT fk_2rxaado1x885ocxl36hllj936;
ALTER TABLE ONLY public.los_uladvice DROP CONSTRAINT fk_2atg4yebbrflop5en1xsvx91y;
ALTER TABLE ONLY public.los_pickingunitload DROP CONSTRAINT fk_2a6uponck83dr84ie2aw1wi9b;
ALTER TABLE ONLY public.los_replenishorder DROP CONSTRAINT fk_1qq9xemkedpxlc7xaxr2cw2uo;
ALTER TABLE ONLY public.mywms_unitload DROP CONSTRAINT fk_1oodxblhpckoyw8v99pb9qd51;
ALTER TABLE ONLY public.los_replenishorder DROP CONSTRAINT fk_1dy042vb1gqwbbumis13h6cet;
ALTER TABLE ONLY public.mywms_itemdata DROP CONSTRAINT fk_14ypmi1k2iash7t1ubev5dtas;
ALTER TABLE ONLY public.los_avisreq DROP CONSTRAINT fk_136624cp6n0n5u6p0anwjg23a;
ALTER TABLE ONLY public.los_pickreceiptpos DROP CONSTRAINT fk_130f7t27b32qhv34x9rmigatn;
ALTER TABLE ONLY public.los_grrposition DROP CONSTRAINT uk_sjpcgr70c1b14n5fj1bwgj2fu;
ALTER TABLE ONLY public.los_itemdata_number DROP CONSTRAINT uk_shxdy9bmv8aaqcp8uyehtr6rq;
ALTER TABLE ONLY public.los_bom DROP CONSTRAINT uk_rx5p8i1ehibhrmfhfvxqmknm3;
ALTER TABLE ONLY public.los_jasperreport DROP CONSTRAINT uk_r9j71doodmxi2ri7gaam7jd7l;
ALTER TABLE ONLY public.mywms_zone DROP CONSTRAINT uk_r8h2wt65kgwdfsbwbgawfyeer;
ALTER TABLE ONLY public.los_storagestrat DROP CONSTRAINT uk_qrejohv6ou22mnsdephm1mp60;
ALTER TABLE ONLY public.mywms_unitload DROP CONSTRAINT uk_pdjgcqlreqny7lsy5ip7jo43r;
ALTER TABLE ONLY public.mywms_itemdata DROP CONSTRAINT uk_paed8her271at4xkb1ae8w0kf;
ALTER TABLE ONLY public.los_avisreq DROP CONSTRAINT uk_ocoj9ehw8n2sdpc5p09t71dbb;
ALTER TABLE ONLY public.los_rack DROP CONSTRAINT uk_njlpjtxw566a1yycr6fgd18u4;
ALTER TABLE ONLY public.mywms_client DROP CONSTRAINT uk_n23h3mdt25nno8wl6c9armsqr;
ALTER TABLE ONLY public.los_outreq DROP CONSTRAINT uk_ml4dwtkudihkygkjkiin11npg;
ALTER TABLE ONLY public.los_customerpos DROP CONSTRAINT uk_mdlrq6f4y98wpcvv0kjx6hgj;
ALTER TABLE ONLY public.los_workingarea DROP CONSTRAINT uk_m342eni5ncgt1j39ea6rrwsua;
ALTER TABLE ONLY public.los_uladvice DROP CONSTRAINT uk_l5qltujupqeptdgmsglnpbct1;
ALTER TABLE ONLY public.los_customerorder DROP CONSTRAINT uk_ksw3yn2ea5uhk3mc5r4n1n9m7;
ALTER TABLE ONLY public.los_uladvice DROP CONSTRAINT uk_ipgs95k1hra5lkxrrmuq2d9he;
ALTER TABLE ONLY public.los_typecapacityconstraint DROP CONSTRAINT uk_hsiwho7ytejho6qjdjcq1rmjk;
ALTER TABLE ONLY public.mywms_client DROP CONSTRAINT uk_ghgin3lnjeaowpuf0qpvp0rw4;
ALTER TABLE ONLY public.los_storagereq DROP CONSTRAINT uk_f55eo4r08ia2f8ysflwioea4y;
ALTER TABLE ONLY public.los_workingareapos DROP CONSTRAINT uk_f1g8indilnpf8kx5ad2q6myud;
ALTER TABLE ONLY public.los_storloc DROP CONSTRAINT uk_emolvlq25quglbkx8qd5s8vhh;
ALTER TABLE ONLY public.mywms_unitloadtype DROP CONSTRAINT uk_eifxf4sfjlf4wf3qw8tudqkpf;
ALTER TABLE ONLY public.los_replenishorder DROP CONSTRAINT uk_c9yikd8kvlu4t45xg8hfbu899;
ALTER TABLE ONLY public.los_orderstrat DROP CONSTRAINT uk_bqervqvxqu8g61pvemek2oy4u;
ALTER TABLE ONLY public.los_stocktaking DROP CONSTRAINT uk_ad3jp6ads3ydnqv5fi6rf0v4s;
ALTER TABLE ONLY public.los_sysprop DROP CONSTRAINT uk_8tcoe23qui9q3ancbhx662iqb;
ALTER TABLE ONLY public.mywms_lot DROP CONSTRAINT uk_7lyjpsmkph4vq3mgt9i0g3477;
ALTER TABLE ONLY public.mywms_role DROP CONSTRAINT uk_6yyotbpw7edc76ejucc4mflf2;
ALTER TABLE ONLY public.mywms_area DROP CONSTRAINT uk_6c1or9ji7c4gb11goh9db9wfu;
ALTER TABLE ONLY public.los_goodsreceipt DROP CONSTRAINT uk_59qpxx4gt30r9bfn6dakqt2av;
ALTER TABLE ONLY public.mywms_request DROP CONSTRAINT uk_52fpfm3f0o24ueleh5jbrf6tn;
ALTER TABLE ONLY public.los_pickingorder DROP CONSTRAINT uk_4jqlc53dlk1fcbifr0e3026vc;
ALTER TABLE ONLY public.los_storagelocationtype DROP CONSTRAINT uk_4c8kfow6cdbi4y69giy0ded2k;
ALTER TABLE ONLY public.mywms_user DROP CONSTRAINT uk_48pkipun1pytmies0wei11bhm;
ALTER TABLE ONLY public.mywms_pluginconfiguration DROP CONSTRAINT uk_434w4iped8p64det9y3yqaia;
ALTER TABLE ONLY public.los_fixassgn DROP CONSTRAINT uk_3yp785ptpbjt85294dvimodx8;
ALTER TABLE ONLY public.los_locationcluster DROP CONSTRAINT uk_32ljdcfc9texdfh39rjouem4k;
ALTER TABLE ONLY public.mywms_document DROP CONSTRAINT uk_2l2tatdxct1bhv2yrtmp1lvdj;
ALTER TABLE ONLY public.los_serviceconf DROP CONSTRAINT uk_1a7ijcl2mx7esj2rmykadasir;
ALTER TABLE ONLY public.mywms_zone DROP CONSTRAINT mywms_zone_pkey;
ALTER TABLE ONLY public.mywms_user DROP CONSTRAINT mywms_user_pkey;
ALTER TABLE ONLY public.mywms_unitloadtype DROP CONSTRAINT mywms_unitloadtype_pkey;
ALTER TABLE ONLY public.mywms_unitload DROP CONSTRAINT mywms_unitload_pkey;
ALTER TABLE ONLY public.mywms_stockunit DROP CONSTRAINT mywms_stockunit_pkey;
ALTER TABLE ONLY public.mywms_role DROP CONSTRAINT mywms_role_pkey;
ALTER TABLE ONLY public.mywms_request DROP CONSTRAINT mywms_request_pkey;
ALTER TABLE ONLY public.mywms_pluginconfiguration DROP CONSTRAINT mywms_pluginconfiguration_pkey;
ALTER TABLE ONLY public.mywms_lot DROP CONSTRAINT mywms_lot_pkey;
ALTER TABLE ONLY public.mywms_logitem DROP CONSTRAINT mywms_logitem_pkey;
ALTER TABLE ONLY public.mywms_itemunit DROP CONSTRAINT mywms_itemunit_pkey;
ALTER TABLE ONLY public.mywms_itemdata DROP CONSTRAINT mywms_itemdata_pkey;
ALTER TABLE ONLY public.mywms_document DROP CONSTRAINT mywms_document_pkey;
ALTER TABLE ONLY public.mywms_client DROP CONSTRAINT mywms_client_pkey;
ALTER TABLE ONLY public.mywms_clearingitem DROP CONSTRAINT mywms_clearingitem_pkey;
ALTER TABLE ONLY public.mywms_area DROP CONSTRAINT mywms_area_pkey;
ALTER TABLE ONLY public.los_workingareapos DROP CONSTRAINT los_workingareapos_pkey;
ALTER TABLE ONLY public.los_workingarea DROP CONSTRAINT los_workingarea_pkey;
ALTER TABLE ONLY public.los_uladvicepos DROP CONSTRAINT los_uladvicepos_pkey;
ALTER TABLE ONLY public.los_uladvice DROP CONSTRAINT los_uladvice_pkey;
ALTER TABLE ONLY public.los_ul_record DROP CONSTRAINT los_ul_record_pkey;
ALTER TABLE ONLY public.los_typecapacityconstraint DROP CONSTRAINT los_typecapacityconstraint_pkey;
ALTER TABLE ONLY public.los_sysprop DROP CONSTRAINT los_sysprop_pkey;
ALTER TABLE ONLY public.los_sulabel DROP CONSTRAINT los_sulabel_pkey;
ALTER TABLE ONLY public.los_storloc DROP CONSTRAINT los_storloc_pkey;
ALTER TABLE ONLY public.los_storagestrat DROP CONSTRAINT los_storagestrat_pkey;
ALTER TABLE ONLY public.los_storagereq DROP CONSTRAINT los_storagereq_pkey;
ALTER TABLE ONLY public.los_storagelocationtype DROP CONSTRAINT los_storagelocationtype_pkey;
ALTER TABLE ONLY public.los_stocktakingrecord DROP CONSTRAINT los_stocktakingrecord_pkey;
ALTER TABLE ONLY public.los_stocktakingorder DROP CONSTRAINT los_stocktakingorder_pkey;
ALTER TABLE ONLY public.los_stocktaking DROP CONSTRAINT los_stocktaking_pkey;
ALTER TABLE ONLY public.los_stockrecord DROP CONSTRAINT los_stockrecord_pkey;
ALTER TABLE ONLY public.los_sllabel DROP CONSTRAINT los_sllabel_pkey;
ALTER TABLE ONLY public.los_serviceconf DROP CONSTRAINT los_serviceconf_pkey;
ALTER TABLE ONLY public.los_sequencenumber DROP CONSTRAINT los_sequencenumber_pkey;
ALTER TABLE ONLY public.los_replenishorder DROP CONSTRAINT los_replenishorder_pkey;
ALTER TABLE ONLY public.los_rack DROP CONSTRAINT los_rack_pkey;
ALTER TABLE ONLY public.los_pickreceiptpos DROP CONSTRAINT los_pickreceiptpos_pkey;
ALTER TABLE ONLY public.los_pickreceipt DROP CONSTRAINT los_pickreceipt_pkey;
ALTER TABLE ONLY public.los_pickingunitload DROP CONSTRAINT los_pickingunitload_pkey;
ALTER TABLE ONLY public.los_pickingpos DROP CONSTRAINT los_pickingpos_pkey;
ALTER TABLE ONLY public.los_pickingorder DROP CONSTRAINT los_pickingorder_pkey;
ALTER TABLE ONLY public.los_outreq DROP CONSTRAINT los_outreq_pkey;
ALTER TABLE ONLY public.los_outpos DROP CONSTRAINT los_outpos_pkey;
ALTER TABLE ONLY public.los_orderstrat DROP CONSTRAINT los_orderstrat_pkey;
ALTER TABLE ONLY public.los_orderreceiptpos DROP CONSTRAINT los_orderreceiptpos_pkey;
ALTER TABLE ONLY public.los_orderreceipt DROP CONSTRAINT los_orderreceipt_pkey;
ALTER TABLE ONLY public.los_locationcluster DROP CONSTRAINT los_locationcluster_pkey;
ALTER TABLE ONLY public.los_jasperreport DROP CONSTRAINT los_jasperreport_pkey;
ALTER TABLE ONLY public.los_itemdata_number DROP CONSTRAINT los_itemdata_number_pkey;
ALTER TABLE ONLY public.los_grrposition DROP CONSTRAINT los_grrposition_pkey;
ALTER TABLE ONLY public.los_goodsreceipt DROP CONSTRAINT los_goodsreceipt_pkey;
ALTER TABLE ONLY public.los_fixassgn DROP CONSTRAINT los_fixassgn_pkey;
ALTER TABLE ONLY public.los_customerpos DROP CONSTRAINT los_customerpos_pkey;
ALTER TABLE ONLY public.los_customerorder DROP CONSTRAINT los_customerorder_pkey;
ALTER TABLE ONLY public.los_bom DROP CONSTRAINT los_bom_pkey;
ALTER TABLE ONLY public.los_avisreq DROP CONSTRAINT los_avisreq_pkey;
DROP SEQUENCE public.seqentities;
DROP TABLE public.mywms_zone;
DROP TABLE public.mywms_user_mywms_role;
DROP TABLE public.mywms_user;
DROP TABLE public.mywms_unitloadtype;
DROP TABLE public.mywms_unitload;
DROP TABLE public.mywms_stockunit;
DROP TABLE public.mywms_role;
DROP TABLE public.mywms_request;
DROP TABLE public.mywms_pluginconfiguration;
DROP TABLE public.mywms_lot;
DROP TABLE public.mywms_logitem;
DROP TABLE public.mywms_itemunit;
DROP TABLE public.mywms_itemdata;
DROP TABLE public.mywms_document;
DROP TABLE public.mywms_client;
DROP TABLE public.mywms_clearingitem;
DROP TABLE public.mywms_area;
DROP TABLE public.los_workingareapos;
DROP TABLE public.los_workingarea;
DROP TABLE public.los_uladvicepos;
DROP TABLE public.los_uladvice;
DROP TABLE public.los_ul_record;
DROP TABLE public.los_typecapacityconstraint;
DROP TABLE public.los_sysprop;
DROP TABLE public.los_sulabel;
DROP TABLE public.los_storloc;
DROP TABLE public.los_storagestrat;
DROP TABLE public.los_storagereq;
DROP TABLE public.los_storagelocationtype;
DROP TABLE public.los_stocktakingrecord;
DROP TABLE public.los_stocktakingorder;
DROP TABLE public.los_stocktaking;
DROP TABLE public.los_stockrecord;
DROP TABLE public.los_sllabel;
DROP TABLE public.los_serviceconf;
DROP TABLE public.los_sequencenumber;
DROP TABLE public.los_replenishorder;
DROP TABLE public.los_rack;
DROP TABLE public.los_pickreceiptpos;
DROP TABLE public.los_pickreceipt;
DROP TABLE public.los_pickingunitload;
DROP TABLE public.los_pickingpos;
DROP TABLE public.los_pickingorder;
DROP TABLE public.los_outreq;
DROP TABLE public.los_outpos;
DROP TABLE public.los_orderstrat;
DROP TABLE public.los_orderreceiptpos;
DROP TABLE public.los_orderreceipt;
DROP TABLE public.los_locationcluster;
DROP TABLE public.los_jasperreport;
DROP TABLE public.los_itemdata_number;
DROP TABLE public.los_grrposition;
DROP TABLE public.los_goodsreceipt_los_avisreq;
DROP TABLE public.los_goodsreceipt;
DROP TABLE public.los_fixassgn;
DROP TABLE public.los_customerpos;
DROP TABLE public.los_customerorder;
DROP TABLE public.los_bom;
DROP TABLE public.los_avisreq;
DROP EXTENSION plpgsql;
DROP SCHEMA public;
--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: los_avisreq; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_avisreq (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    advicenumber character varying(255) NOT NULL,
    advicestate character varying(255),
    expecteddelivery date,
    expirebatch boolean NOT NULL,
    externalno character varying(255),
    externalid character varying(255),
    finishdate timestamp without time zone,
    notifiedamount numeric(17,4),
    processdate timestamp without time zone,
    receiptamount numeric(17,4),
    client_id bigint NOT NULL,
    itemdata_id bigint NOT NULL,
    lot_id bigint
);


ALTER TABLE public.los_avisreq OWNER TO jboss;

--
-- Name: los_bom; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_bom (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4) NOT NULL,
    index integer NOT NULL,
    pickable boolean NOT NULL,
    child_id bigint NOT NULL,
    parent_id bigint NOT NULL
);


ALTER TABLE public.los_bom OWNER TO jboss;

--
-- Name: los_customerorder; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_customerorder (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    customername character varying(255),
    customernumber character varying(255),
    delivery date,
    documenturl character varying(255),
    dtype character varying(255),
    externalid character varying(255),
    externalnumber character varying(255),
    labelurl character varying(255),
    number character varying(255),
    prio integer NOT NULL,
    state integer NOT NULL,
    client_id bigint NOT NULL,
    destination_id bigint,
    strategy_id bigint NOT NULL
);


ALTER TABLE public.los_customerorder OWNER TO jboss;

--
-- Name: los_customerpos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_customerpos (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4) NOT NULL,
    amountpicked numeric(17,4) NOT NULL,
    externalid character varying(255),
    index integer NOT NULL,
    number character varying(255),
    partitionallowed boolean NOT NULL,
    serialnumber character varying(255),
    state integer NOT NULL,
    client_id bigint NOT NULL,
    itemdata_id bigint NOT NULL,
    lot_id bigint,
    order_id bigint NOT NULL
);


ALTER TABLE public.los_customerpos OWNER TO jboss;

--
-- Name: los_fixassgn; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_fixassgn (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    desiredamount numeric(17,4) NOT NULL,
    assignedlocation_id bigint NOT NULL,
    itemdata_id bigint NOT NULL
);


ALTER TABLE public.los_fixassgn OWNER TO jboss;

--
-- Name: los_goodsreceipt; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_goodsreceipt (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    delnote character varying(255),
    drivername character varying(255),
    forwarder character varying(255),
    gr_number character varying(255) NOT NULL,
    licenceplate character varying(255),
    receiptdate date,
    receiptstate character varying(255),
    referenceno character varying(255),
    client_id bigint NOT NULL,
    goodsinlocation_id bigint,
    operator_id bigint
);


ALTER TABLE public.los_goodsreceipt OWNER TO jboss;

--
-- Name: los_goodsreceipt_los_avisreq; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_goodsreceipt_los_avisreq (
    los_goodsreceipt_id bigint NOT NULL,
    assignedadvices_id bigint NOT NULL
);


ALTER TABLE public.los_goodsreceipt_los_avisreq OWNER TO jboss;

--
-- Name: los_grrposition; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_grrposition (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4),
    itemdata character varying(255),
    lot character varying(255),
    orderreference character varying(255),
    positionnumber character varying(255),
    qafault character varying(1024),
    qalock integer NOT NULL,
    receipttype character varying(255),
    scale integer NOT NULL,
    state integer,
    stockunitstr character varying(255),
    unitload character varying(255),
    client_id bigint NOT NULL,
    goodsreceipt_id bigint NOT NULL,
    operator_id bigint,
    relatedadvice_id bigint,
    stockunit_id bigint
);


ALTER TABLE public.los_grrposition OWNER TO jboss;

--
-- Name: los_itemdata_number; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_itemdata_number (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    index integer NOT NULL,
    manufacturername character varying(255),
    number character varying(255) NOT NULL,
    client_id bigint NOT NULL,
    itemdata_id bigint NOT NULL
);


ALTER TABLE public.los_itemdata_number OWNER TO jboss;

--
-- Name: los_jasperreport; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_jasperreport (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    compileddocument oid,
    name character varying(255) NOT NULL,
    sourcedocument text,
    client_id bigint NOT NULL
);


ALTER TABLE public.los_jasperreport OWNER TO jboss;

--
-- Name: los_locationcluster; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_locationcluster (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    level integer,
    name character varying(255) NOT NULL
);


ALTER TABLE public.los_locationcluster OWNER TO jboss;

--
-- Name: los_orderreceipt; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_orderreceipt (
    date date NOT NULL,
    destination character varying(255),
    ordernumber character varying(255) NOT NULL,
    orderreference character varying(255),
    ordertype character varying(255) NOT NULL,
    state character varying(255),
    user_ character varying(255) NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE public.los_orderreceipt OWNER TO jboss;

--
-- Name: los_orderreceiptpos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_orderreceiptpos (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4),
    amountordered numeric(17,4),
    articledescr character varying(255),
    articleref character varying(255),
    articlescale integer NOT NULL,
    lotref character varying(255),
    pos integer NOT NULL,
    client_id bigint NOT NULL,
    receipt_id bigint
);


ALTER TABLE public.los_orderreceiptpos OWNER TO jboss;

--
-- Name: los_orderstrat; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_orderstrat (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    createfollowuppicks boolean NOT NULL,
    creategoodsoutorder boolean NOT NULL,
    manualcreationindex integer NOT NULL,
    name character varying(255),
    prefermatchingstock boolean NOT NULL,
    preferunopened boolean NOT NULL,
    uselockedlot boolean NOT NULL,
    uselockedstock boolean NOT NULL,
    client_id bigint NOT NULL,
    defaultdestination_id bigint
);


ALTER TABLE public.los_orderstrat OWNER TO jboss;

--
-- Name: los_outpos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_outpos (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    outstate character varying(255),
    goodsoutrequest_id bigint NOT NULL,
    source_id bigint NOT NULL
);


ALTER TABLE public.los_outpos OWNER TO jboss;

--
-- Name: los_outreq; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_outreq (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    courier character varying(255),
    externalnumber character varying(255),
    groupname character varying(255),
    number character varying(255) NOT NULL,
    outstate character varying(255),
    shippingdate timestamp without time zone,
    client_id bigint NOT NULL,
    customerorder_id bigint,
    operator_id bigint,
    outlocation_id bigint
);


ALTER TABLE public.los_outreq OWNER TO jboss;

--
-- Name: los_pickingorder; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_pickingorder (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    customerordernumber character varying(255),
    manualcreation boolean NOT NULL,
    number character varying(255) NOT NULL,
    prio integer NOT NULL,
    state integer NOT NULL,
    client_id bigint NOT NULL,
    destination_id bigint,
    operator_id bigint,
    strategy_id bigint NOT NULL
);


ALTER TABLE public.los_pickingorder OWNER TO jboss;

--
-- Name: los_pickingpos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_pickingpos (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4) NOT NULL,
    amountpicked numeric(17,4) NOT NULL,
    pickfromlocationname character varying(255),
    pickfromunitloadlabel character varying(255),
    pickingordernumber character varying(255),
    pickingtype integer NOT NULL,
    state integer NOT NULL,
    client_id bigint NOT NULL,
    customerorderposition_id bigint,
    itemdata_id bigint NOT NULL,
    lotpicked_id bigint,
    pickfromstockunit_id bigint,
    picktounitload_id bigint,
    pickingorder_id bigint,
    strategy_id bigint NOT NULL
);


ALTER TABLE public.los_pickingpos OWNER TO jboss;

--
-- Name: los_pickingunitload; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_pickingunitload (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    customerordernumber character varying(255),
    positionindex integer NOT NULL,
    state integer NOT NULL,
    client_id bigint NOT NULL,
    pickingorder_id bigint NOT NULL,
    unitload_id bigint NOT NULL
);


ALTER TABLE public.los_pickingunitload OWNER TO jboss;

--
-- Name: los_pickreceipt; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_pickreceipt (
    date date,
    labelid character varying(255),
    ordernumber character varying(255),
    picknumber character varying(255),
    state character varying(255),
    id bigint NOT NULL
);


ALTER TABLE public.los_pickreceipt OWNER TO jboss;

--
-- Name: los_pickreceiptpos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_pickreceiptpos (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4),
    amountordered numeric(17,4),
    articledescr character varying(255),
    articleref character varying(255),
    lotref character varying(255),
    receipt_id bigint
);


ALTER TABLE public.los_pickreceiptpos OWNER TO jboss;

--
-- Name: los_rack; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_rack (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    aisle character varying(255),
    labeloffset integer,
    rname character varying(255) NOT NULL,
    numberofcolumns integer NOT NULL,
    numberofrows integer NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.los_rack OWNER TO jboss;

--
-- Name: los_replenishorder; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_replenishorder (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    number character varying(255) NOT NULL,
    prio integer NOT NULL,
    requestedamount numeric(17,4),
    sourcelocationname character varying(255),
    state integer NOT NULL,
    client_id bigint NOT NULL,
    destination_id bigint,
    itemdata_id bigint NOT NULL,
    lot_id bigint,
    operator_id bigint,
    requestedlocation_id bigint,
    requestedrack_id bigint,
    stockunit_id bigint
);


ALTER TABLE public.los_replenishorder OWNER TO jboss;

--
-- Name: los_sequencenumber; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_sequencenumber (
    classname character varying(255) NOT NULL,
    sequencenumber bigint NOT NULL,
    version integer NOT NULL
);


ALTER TABLE public.los_sequencenumber OWNER TO jboss;

--
-- Name: los_serviceconf; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_serviceconf (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    servkey character varying(255) NOT NULL,
    service character varying(255) NOT NULL,
    subkey character varying(255),
    servvalue character varying(255),
    client_id bigint NOT NULL
);


ALTER TABLE public.los_serviceconf OWNER TO jboss;

--
-- Name: los_sllabel; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_sllabel (
    labelid character varying(255),
    id bigint NOT NULL
);


ALTER TABLE public.los_sllabel OWNER TO jboss;

--
-- Name: los_stockrecord; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_stockrecord (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    activitycode character varying(255) NOT NULL,
    amount numeric(17,4),
    amountstock numeric(17,4),
    fromstockunitidentity character varying(255),
    fromstoragelocation character varying(255) NOT NULL,
    fromunitload character varying(255),
    itemdata character varying(255),
    lot character varying(255),
    operator character varying(255) NOT NULL,
    scale integer NOT NULL,
    serialnumber character varying(255),
    tostockunitidentity character varying(255),
    tostoragelocation character varying(255) NOT NULL,
    tounitload character varying(255),
    type character varying(255),
    unitloadtype character varying(255),
    client_id bigint NOT NULL
);


ALTER TABLE public.los_stockrecord OWNER TO jboss;

--
-- Name: los_stocktaking; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_stocktaking (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    ended timestamp without time zone,
    started timestamp without time zone,
    stocktakingnumber character varying(255) NOT NULL,
    stocktakingtype character varying(255)
);


ALTER TABLE public.los_stocktaking OWNER TO jboss;

--
-- Name: los_stocktakingorder; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_stocktakingorder (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    areaname character varying(255),
    countingdate timestamp without time zone,
    locationname character varying(255),
    operator character varying(255),
    state integer,
    unitloadlabel character varying(255),
    stocktaking_id bigint
);


ALTER TABLE public.los_stocktakingorder OWNER TO jboss;

--
-- Name: los_stocktakingrecord; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_stocktakingrecord (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    clientno character varying(255),
    countedquantity numeric(19,2),
    countedstockid bigint,
    itemno character varying(255),
    locationname character varying(255),
    lotno character varying(255),
    plannedquantity numeric(19,2),
    serialno character varying(255),
    state integer,
    ultypeno character varying(255),
    unitloadlabel character varying(255),
    stocktakingorder_id bigint
);


ALTER TABLE public.los_stocktakingrecord OWNER TO jboss;

--
-- Name: los_storagelocationtype; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_storagelocationtype (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    depth numeric(15,2),
    handlingflag integer NOT NULL,
    height numeric(15,2),
    liftingcapacity numeric(16,3),
    sltname character varying(255) NOT NULL,
    volume numeric(19,6),
    width numeric(15,2)
);


ALTER TABLE public.los_storagelocationtype OWNER TO jboss;

--
-- Name: los_storagereq; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_storagereq (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    number character varying(255) NOT NULL,
    requeststate character varying(255),
    client_id bigint NOT NULL,
    destination_id bigint,
    unitload_id bigint NOT NULL
);


ALTER TABLE public.los_storagereq OWNER TO jboss;

--
-- Name: los_storagestrat; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_storagestrat (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    clientmode integer NOT NULL,
    mixclient boolean NOT NULL,
    mixitem boolean NOT NULL,
    name character varying(255) NOT NULL,
    orderbymode integer NOT NULL,
    useitemzone boolean NOT NULL,
    usepicking integer NOT NULL,
    usestorage integer NOT NULL,
    zone_id bigint
);


ALTER TABLE public.los_storagestrat OWNER TO jboss;

--
-- Name: los_storloc; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_storloc (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    xpos integer NOT NULL,
    ypos integer NOT NULL,
    zpos integer NOT NULL,
    allocation numeric(15,2) NOT NULL,
    allocationstate integer NOT NULL,
    field character varying(255),
    fieldindex integer NOT NULL,
    name character varying(255) NOT NULL,
    orderindex integer NOT NULL,
    plccode character varying(255),
    scancode character varying(255) NOT NULL,
    stocktakingdate timestamp without time zone,
    client_id bigint NOT NULL,
    area_id bigint NOT NULL,
    cluster_id bigint,
    currenttcc bigint,
    rack_id bigint,
    type_id bigint NOT NULL,
    zone_id bigint
);


ALTER TABLE public.los_storloc OWNER TO jboss;

--
-- Name: los_sulabel; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_sulabel (
    amount numeric(19,2) NOT NULL,
    clientref character varying(255) NOT NULL,
    dateref character varying(255) NOT NULL,
    itemunit character varying(255) NOT NULL,
    itemdataref character varying(255) NOT NULL,
    labelid character varying(255) NOT NULL,
    lotref character varying(255),
    scale integer NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE public.los_sulabel OWNER TO jboss;

--
-- Name: los_sysprop; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_sysprop (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    description character varying(255),
    groupname character varying(255),
    hidden boolean NOT NULL,
    syskey character varying(255) NOT NULL,
    sysvalue character varying(255),
    workstation character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.los_sysprop OWNER TO jboss;

--
-- Name: los_typecapacityconstraint; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_typecapacityconstraint (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    allocation numeric(15,2) NOT NULL,
    allocationtype integer NOT NULL,
    orderindex integer NOT NULL,
    storagelocationtype_id bigint NOT NULL,
    unitloadtype_id bigint NOT NULL
);


ALTER TABLE public.los_typecapacityconstraint OWNER TO jboss;

--
-- Name: los_ul_record; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_ul_record (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    activitycode character varying(255),
    fromlocation character varying(255) NOT NULL,
    label character varying(255) NOT NULL,
    operator character varying(255),
    recordtype character varying(255) NOT NULL,
    tolocation character varying(255) NOT NULL,
    unitloadtype character varying(255),
    client_id bigint NOT NULL
);


ALTER TABLE public.los_ul_record OWNER TO jboss;

--
-- Name: los_uladvice; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_uladvice (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    advicestate character varying(255) NOT NULL,
    advicetype character varying(255) NOT NULL,
    externalnumber character varying(255),
    labelid character varying(255) NOT NULL,
    number character varying(255) NOT NULL,
    reasonforreturn character varying(255),
    switchstateinfo character varying(255),
    client_id bigint NOT NULL,
    relatedadvice_id bigint,
    unitloadtype_id bigint
);


ALTER TABLE public.los_uladvice OWNER TO jboss;

--
-- Name: los_uladvicepos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_uladvicepos (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    notifiedamount numeric(17,4) NOT NULL,
    positionnumber character varying(255) NOT NULL,
    client_id bigint NOT NULL,
    itemdata_id bigint NOT NULL,
    lot_id bigint,
    unitloadadvice_id bigint NOT NULL
);


ALTER TABLE public.los_uladvicepos OWNER TO jboss;

--
-- Name: los_workingarea; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_workingarea (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.los_workingarea OWNER TO jboss;

--
-- Name: los_workingareapos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_workingareapos (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    cluster_id bigint NOT NULL,
    workingarea_id bigint NOT NULL
);


ALTER TABLE public.los_workingareapos OWNER TO jboss;

--
-- Name: mywms_area; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_area (
    dtype character varying(31) NOT NULL,
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    name character varying(255) NOT NULL,
    useforgoodsin boolean NOT NULL,
    useforgoodsout boolean NOT NULL,
    useforpicking boolean NOT NULL,
    useforreplenish boolean NOT NULL,
    useforstorage boolean NOT NULL,
    usefortransfer boolean NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_area OWNER TO jboss;

--
-- Name: mywms_clearingitem; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_clearingitem (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    bundleresolver oid,
    host character varying(255) NOT NULL,
    messageparameters bytea NOT NULL,
    messageresourcekey character varying(255) NOT NULL,
    options oid NOT NULL,
    propertymap bytea,
    resourcebundlename character varying(255) NOT NULL,
    shortmessageparameters bytea NOT NULL,
    shortmessageresourcekey character varying(255) NOT NULL,
    solution oid,
    solver character varying(255),
    source character varying(255) NOT NULL,
    user_ character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_clearingitem OWNER TO jboss;

--
-- Name: mywms_client; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_client (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    cl_code character varying(255),
    email character varying(255),
    fax character varying(255),
    name character varying(255) NOT NULL,
    cl_nr character varying(255) NOT NULL,
    phone character varying(255)
);


ALTER TABLE public.mywms_client OWNER TO jboss;

--
-- Name: mywms_document; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_document (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    document oid NOT NULL,
    name character varying(255) NOT NULL,
    document_size integer,
    type character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_document OWNER TO jboss;

--
-- Name: mywms_itemdata; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_itemdata (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    advicemandatory boolean NOT NULL,
    depth numeric(15,2),
    descr character varying(255),
    height numeric(15,2),
    lotmandatory boolean NOT NULL,
    lotsubstitutiontype character varying(255),
    name character varying(255) NOT NULL,
    item_nr character varying(255) NOT NULL,
    rest_usage_gi integer,
    safetystock integer NOT NULL,
    scale integer NOT NULL,
    serialrectype character varying(255) NOT NULL,
    tradegroup character varying(255),
    volume numeric(19,6),
    weight numeric(16,3),
    width numeric(15,2),
    client_id bigint NOT NULL,
    defultype_id bigint,
    handlingunit_id bigint NOT NULL,
    zone_id bigint
);


ALTER TABLE public.mywms_itemdata OWNER TO jboss;

--
-- Name: mywms_itemunit; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_itemunit (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    basefactor integer NOT NULL,
    unitname character varying(255) NOT NULL,
    unittype character varying(255),
    baseunit_id bigint
);


ALTER TABLE public.mywms_itemunit OWNER TO jboss;

--
-- Name: mywms_logitem; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_logitem (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    bundleresolver character varying(255),
    host character varying(255) NOT NULL,
    message character varying(255) NOT NULL,
    messageparameters bytea,
    messageresourcekey character varying(255) NOT NULL,
    resourcebundlename character varying(255) NOT NULL,
    source character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    user_ character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_logitem OWNER TO jboss;

--
-- Name: mywms_lot; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_lot (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    age character varying(255),
    bestbeforeend date,
    code character varying(255),
    lot_date date NOT NULL,
    depth numeric(15,2),
    height numeric(15,2),
    name character varying(255),
    usenotbefore date,
    volume numeric(19,6),
    weight numeric(16,3),
    width numeric(15,2),
    client_id bigint NOT NULL,
    itemdata_id bigint NOT NULL
);


ALTER TABLE public.mywms_lot OWNER TO jboss;

--
-- Name: mywms_pluginconfiguration; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_pluginconfiguration (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    plugin_mode character varying(255) NOT NULL,
    pluginclass character varying(255) NOT NULL,
    pluginname character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_pluginconfiguration OWNER TO jboss;

--
-- Name: mywms_request; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_request (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    request_nr character varying(255) NOT NULL,
    parentrequestnumber character varying(255),
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_request OWNER TO jboss;

--
-- Name: mywms_role; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_role (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    description character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.mywms_role OWNER TO jboss;

--
-- Name: mywms_stockunit; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_stockunit (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4) NOT NULL,
    reservedamount numeric(17,4),
    serialnumber character varying(255),
    strategydate date NOT NULL,
    client_id bigint NOT NULL,
    itemdata_id bigint NOT NULL,
    lot_id bigint,
    unitload_id bigint NOT NULL
);


ALTER TABLE public.mywms_stockunit OWNER TO jboss;

--
-- Name: mywms_unitload; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_unitload (
    dtype character varying(31) NOT NULL,
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    location_index integer,
    labelid character varying(255) NOT NULL,
    carrier boolean NOT NULL,
    carrierunitloadid bigint,
    opened boolean NOT NULL,
    packagetype character varying(255),
    stocktakingdate timestamp without time zone,
    weight numeric(16,3),
    weightcalculated numeric(16,3),
    weightmeasure numeric(16,3),
    client_id bigint NOT NULL,
    type_id bigint NOT NULL,
    carrierunitload_id bigint,
    storagelocation_id bigint NOT NULL
);


ALTER TABLE public.mywms_unitload OWNER TO jboss;

--
-- Name: mywms_unitloadtype; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_unitloadtype (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    depth numeric(15,2),
    height numeric(15,2),
    liftingcapacity numeric(16,3),
    name character varying(255) NOT NULL,
    volume numeric(19,6),
    weight numeric(16,3),
    width numeric(15,2)
);


ALTER TABLE public.mywms_unitloadtype OWNER TO jboss;

--
-- Name: mywms_user; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_user (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    email character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    locale character varying(255),
    name character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    phone character varying(255),
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_user OWNER TO jboss;

--
-- Name: mywms_user_mywms_role; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_user_mywms_role (
    mywms_user_id bigint NOT NULL,
    roles_id bigint NOT NULL
);


ALTER TABLE public.mywms_user_mywms_role OWNER TO jboss;

--
-- Name: mywms_zone; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_zone (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    name character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_zone OWNER TO jboss;

--
-- Name: seqentities; Type: SEQUENCE; Schema: public; Owner: jboss
--

CREATE SEQUENCE seqentities
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seqentities OWNER TO jboss;

--
-- Data for Name: los_avisreq; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_avisreq (id, additionalcontent, created, entity_lock, modified, version, advicenumber, advicestate, expecteddelivery, expirebatch, externalno, externalid, finishdate, notifiedamount, processdate, receiptamount, client_id, itemdata_id, lot_id) FROM stdin;
\.


--
-- Data for Name: los_bom; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_bom (id, additionalcontent, created, entity_lock, modified, version, amount, index, pickable, child_id, parent_id) FROM stdin;
\.


--
-- Data for Name: los_customerorder; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_customerorder (id, additionalcontent, created, entity_lock, modified, version, customername, customernumber, delivery, documenturl, dtype, externalid, externalnumber, labelurl, number, prio, state, client_id, destination_id, strategy_id) FROM stdin;
\.


--
-- Data for Name: los_customerpos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_customerpos (id, additionalcontent, created, entity_lock, modified, version, amount, amountpicked, externalid, index, number, partitionallowed, serialnumber, state, client_id, itemdata_id, lot_id, order_id) FROM stdin;
\.


--
-- Data for Name: los_fixassgn; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_fixassgn (id, additionalcontent, created, entity_lock, modified, version, desiredamount, assignedlocation_id, itemdata_id) FROM stdin;
\.


--
-- Data for Name: los_goodsreceipt; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_goodsreceipt (id, additionalcontent, created, entity_lock, modified, version, delnote, drivername, forwarder, gr_number, licenceplate, receiptdate, receiptstate, referenceno, client_id, goodsinlocation_id, operator_id) FROM stdin;
\.


--
-- Data for Name: los_goodsreceipt_los_avisreq; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_goodsreceipt_los_avisreq (los_goodsreceipt_id, assignedadvices_id) FROM stdin;
\.


--
-- Data for Name: los_grrposition; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_grrposition (id, additionalcontent, created, entity_lock, modified, version, amount, itemdata, lot, orderreference, positionnumber, qafault, qalock, receipttype, scale, state, stockunitstr, unitload, client_id, goodsreceipt_id, operator_id, relatedadvice_id, stockunit_id) FROM stdin;
\.


--
-- Data for Name: los_itemdata_number; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_itemdata_number (id, additionalcontent, created, entity_lock, modified, version, index, manufacturername, number, client_id, itemdata_id) FROM stdin;
\.


--
-- Data for Name: los_jasperreport; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_jasperreport (id, additionalcontent, created, entity_lock, modified, version, compileddocument, name, sourcedocument, client_id) FROM stdin;
\.


--
-- Data for Name: los_locationcluster; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_locationcluster (id, additionalcontent, created, entity_lock, modified, version, level, name) FROM stdin;
\.


--
-- Data for Name: los_orderreceipt; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_orderreceipt (date, destination, ordernumber, orderreference, ordertype, state, user_, id) FROM stdin;
\.


--
-- Data for Name: los_orderreceiptpos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_orderreceiptpos (id, additionalcontent, created, entity_lock, modified, version, amount, amountordered, articledescr, articleref, articlescale, lotref, pos, client_id, receipt_id) FROM stdin;
\.


--
-- Data for Name: los_orderstrat; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_orderstrat (id, additionalcontent, created, entity_lock, modified, version, createfollowuppicks, creategoodsoutorder, manualcreationindex, name, prefermatchingstock, preferunopened, uselockedlot, uselockedstock, client_id, defaultdestination_id) FROM stdin;
\.


--
-- Data for Name: los_outpos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_outpos (id, additionalcontent, created, entity_lock, modified, version, outstate, goodsoutrequest_id, source_id) FROM stdin;
\.


--
-- Data for Name: los_outreq; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_outreq (id, additionalcontent, created, entity_lock, modified, version, courier, externalnumber, groupname, number, outstate, shippingdate, client_id, customerorder_id, operator_id, outlocation_id) FROM stdin;
\.


--
-- Data for Name: los_pickingorder; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_pickingorder (id, additionalcontent, created, entity_lock, modified, version, customerordernumber, manualcreation, number, prio, state, client_id, destination_id, operator_id, strategy_id) FROM stdin;
\.


--
-- Data for Name: los_pickingpos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_pickingpos (id, additionalcontent, created, entity_lock, modified, version, amount, amountpicked, pickfromlocationname, pickfromunitloadlabel, pickingordernumber, pickingtype, state, client_id, customerorderposition_id, itemdata_id, lotpicked_id, pickfromstockunit_id, picktounitload_id, pickingorder_id, strategy_id) FROM stdin;
\.


--
-- Data for Name: los_pickingunitload; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_pickingunitload (id, additionalcontent, created, entity_lock, modified, version, customerordernumber, positionindex, state, client_id, pickingorder_id, unitload_id) FROM stdin;
\.


--
-- Data for Name: los_pickreceipt; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_pickreceipt (date, labelid, ordernumber, picknumber, state, id) FROM stdin;
\.


--
-- Data for Name: los_pickreceiptpos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_pickreceiptpos (id, additionalcontent, created, entity_lock, modified, version, amount, amountordered, articledescr, articleref, lotref, receipt_id) FROM stdin;
\.


--
-- Data for Name: los_rack; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_rack (id, additionalcontent, created, entity_lock, modified, version, aisle, labeloffset, rname, numberofcolumns, numberofrows, client_id) FROM stdin;
\.


--
-- Data for Name: los_replenishorder; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_replenishorder (id, additionalcontent, created, entity_lock, modified, version, number, prio, requestedamount, sourcelocationname, state, client_id, destination_id, itemdata_id, lot_id, operator_id, requestedlocation_id, requestedrack_id, stockunit_id) FROM stdin;
\.


--
-- Data for Name: los_sequencenumber; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_sequencenumber (classname, sequencenumber, version) FROM stdin;
\.


--
-- Data for Name: los_serviceconf; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_serviceconf (id, additionalcontent, created, entity_lock, modified, version, servkey, service, subkey, servvalue, client_id) FROM stdin;
\.


--
-- Data for Name: los_sllabel; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_sllabel (labelid, id) FROM stdin;
\.


--
-- Data for Name: los_stockrecord; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_stockrecord (id, additionalcontent, created, entity_lock, modified, version, activitycode, amount, amountstock, fromstockunitidentity, fromstoragelocation, fromunitload, itemdata, lot, operator, scale, serialnumber, tostockunitidentity, tostoragelocation, tounitload, type, unitloadtype, client_id) FROM stdin;
\.


--
-- Data for Name: los_stocktaking; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_stocktaking (id, additionalcontent, created, entity_lock, modified, version, ended, started, stocktakingnumber, stocktakingtype) FROM stdin;
\.


--
-- Data for Name: los_stocktakingorder; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_stocktakingorder (id, additionalcontent, created, entity_lock, modified, version, areaname, countingdate, locationname, operator, state, unitloadlabel, stocktaking_id) FROM stdin;
\.


--
-- Data for Name: los_stocktakingrecord; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_stocktakingrecord (id, additionalcontent, created, entity_lock, modified, version, clientno, countedquantity, countedstockid, itemno, locationname, lotno, plannedquantity, serialno, state, ultypeno, unitloadlabel, stocktakingorder_id) FROM stdin;
\.


--
-- Data for Name: los_storagelocationtype; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_storagelocationtype (id, additionalcontent, created, entity_lock, modified, version, depth, handlingflag, height, liftingcapacity, sltname, volume, width) FROM stdin;
\.


--
-- Data for Name: los_storagereq; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_storagereq (id, additionalcontent, created, entity_lock, modified, version, number, requeststate, client_id, destination_id, unitload_id) FROM stdin;
\.


--
-- Data for Name: los_storagestrat; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_storagestrat (id, additionalcontent, created, entity_lock, modified, version, clientmode, mixclient, mixitem, name, orderbymode, useitemzone, usepicking, usestorage, zone_id) FROM stdin;
\.


--
-- Data for Name: los_storloc; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_storloc (id, additionalcontent, created, entity_lock, modified, version, xpos, ypos, zpos, allocation, allocationstate, field, fieldindex, name, orderindex, plccode, scancode, stocktakingdate, client_id, area_id, cluster_id, currenttcc, rack_id, type_id, zone_id) FROM stdin;
\.


--
-- Data for Name: los_sulabel; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_sulabel (amount, clientref, dateref, itemunit, itemdataref, labelid, lotref, scale, id) FROM stdin;
\.


--
-- Data for Name: los_sysprop; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_sysprop (id, additionalcontent, created, entity_lock, modified, version, description, groupname, hidden, syskey, sysvalue, workstation, client_id) FROM stdin;
\.


--
-- Data for Name: los_typecapacityconstraint; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_typecapacityconstraint (id, additionalcontent, created, entity_lock, modified, version, allocation, allocationtype, orderindex, storagelocationtype_id, unitloadtype_id) FROM stdin;
\.


--
-- Data for Name: los_ul_record; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_ul_record (id, additionalcontent, created, entity_lock, modified, version, activitycode, fromlocation, label, operator, recordtype, tolocation, unitloadtype, client_id) FROM stdin;
\.


--
-- Data for Name: los_uladvice; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_uladvice (id, additionalcontent, created, entity_lock, modified, version, advicestate, advicetype, externalnumber, labelid, number, reasonforreturn, switchstateinfo, client_id, relatedadvice_id, unitloadtype_id) FROM stdin;
\.


--
-- Data for Name: los_uladvicepos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_uladvicepos (id, additionalcontent, created, entity_lock, modified, version, notifiedamount, positionnumber, client_id, itemdata_id, lot_id, unitloadadvice_id) FROM stdin;
\.


--
-- Data for Name: los_workingarea; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_workingarea (id, additionalcontent, created, entity_lock, modified, version, name) FROM stdin;
\.


--
-- Data for Name: los_workingareapos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_workingareapos (id, additionalcontent, created, entity_lock, modified, version, cluster_id, workingarea_id) FROM stdin;
\.


--
-- Data for Name: mywms_area; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_area (dtype, id, additionalcontent, created, entity_lock, modified, version, name, useforgoodsin, useforgoodsout, useforpicking, useforreplenish, useforstorage, usefortransfer, client_id) FROM stdin;
\.


--
-- Data for Name: mywms_clearingitem; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_clearingitem (id, additionalcontent, created, entity_lock, modified, version, bundleresolver, host, messageparameters, messageresourcekey, options, propertymap, resourcebundlename, shortmessageparameters, shortmessageresourcekey, solution, solver, source, user_, client_id) FROM stdin;
\.


--
-- Data for Name: mywms_client; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_client (id, additionalcontent, created, entity_lock, modified, version, cl_code, email, fax, name, cl_nr, phone) FROM stdin;
0	This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it. But feel free to choose a suitable name.	2015-05-13 09:13:29.662774	0	2015-05-13 09:13:29.662774	0	System-Client	\N	\N	System-Client	System	\N
\.


--
-- Data for Name: mywms_document; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_document (id, additionalcontent, created, entity_lock, modified, version, document, name, document_size, type, client_id) FROM stdin;
\.


--
-- Data for Name: mywms_itemdata; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_itemdata (id, additionalcontent, created, entity_lock, modified, version, advicemandatory, depth, descr, height, lotmandatory, lotsubstitutiontype, name, item_nr, rest_usage_gi, safetystock, scale, serialrectype, tradegroup, volume, weight, width, client_id, defultype_id, handlingunit_id, zone_id) FROM stdin;
\.


--
-- Data for Name: mywms_itemunit; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_itemunit (id, additionalcontent, created, entity_lock, modified, version, basefactor, unitname, unittype, baseunit_id) FROM stdin;
\.


--
-- Data for Name: mywms_logitem; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_logitem (id, additionalcontent, created, entity_lock, modified, version, bundleresolver, host, message, messageparameters, messageresourcekey, resourcebundlename, source, type, user_, client_id) FROM stdin;
\.


--
-- Data for Name: mywms_lot; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_lot (id, additionalcontent, created, entity_lock, modified, version, age, bestbeforeend, code, lot_date, depth, height, name, usenotbefore, volume, weight, width, client_id, itemdata_id) FROM stdin;
\.


--
-- Data for Name: mywms_pluginconfiguration; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_pluginconfiguration (id, additionalcontent, created, entity_lock, modified, version, plugin_mode, pluginclass, pluginname, client_id) FROM stdin;
\.


--
-- Data for Name: mywms_request; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_request (id, additionalcontent, created, entity_lock, modified, version, request_nr, parentrequestnumber, client_id) FROM stdin;
\.


--
-- Data for Name: mywms_role; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_role (id, additionalcontent, created, entity_lock, modified, version, description, name) FROM stdin;
0	This is a system used entity. DO NOT REMOVE, LOCK OR RENAME IT! Some processes may use it.	2015-05-13 09:13:29.662774	0	2015-05-13 09:13:29.662774	0	System Administrator	Admin
\.


--
-- Data for Name: mywms_stockunit; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_stockunit (id, additionalcontent, created, entity_lock, modified, version, amount, reservedamount, serialnumber, strategydate, client_id, itemdata_id, lot_id, unitload_id) FROM stdin;
\.


--
-- Data for Name: mywms_unitload; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_unitload (dtype, id, additionalcontent, created, entity_lock, modified, version, location_index, labelid, carrier, carrierunitloadid, opened, packagetype, stocktakingdate, weight, weightcalculated, weightmeasure, client_id, type_id, carrierunitload_id, storagelocation_id) FROM stdin;
\.


--
-- Data for Name: mywms_unitloadtype; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_unitloadtype (id, additionalcontent, created, entity_lock, modified, version, depth, height, liftingcapacity, name, volume, weight, width) FROM stdin;
\.


--
-- Data for Name: mywms_user; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_user (id, additionalcontent, created, entity_lock, modified, version, email, firstname, lastname, locale, name, password, phone, client_id) FROM stdin;
0	This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it. But feel free to choose a suitable name and password.	2015-05-13 09:13:29.662774	0	2015-05-13 09:13:29.662774	0	\N	\N	\N	en	admin	21232f297a57a5a743894a0e4a801fc3	\N	0
1	\N	2015-05-13 09:13:29.662774	0	2015-05-13 09:13:29.662774	0	\N	\N	\N	de	deutsch	09c438e63455e3e1b3deabe65fdbc087	\N	0
6	\N	2015-05-13 09:13:29.662774	0	2015-05-13 09:13:29.662774	0	\N	\N	\N	de	de	5f02f0889301fd7be1ac972c11bf3e7d	\N	0
2	\N	2015-05-13 09:13:29.662774	0	2015-05-13 09:13:29.662774	0	\N	\N	\N	en	english	ba0a6ddd94c73698a3658f92ac222f8a	\N	0
7	\N	2015-05-13 09:13:29.662774	0	2015-05-13 09:13:29.662774	0	\N	\N	\N	en	en	9cfefed8fb9497baa5cd519d7d2bb5d7	\N	0
8	\N	2015-05-13 09:13:29.662774	0	2015-05-13 09:13:29.662774	0	\N	\N	\N	fr	fr	82a9e4d26595c87ab6e442391d8c5bba	\N	0
9	\N	2015-05-13 09:13:29.662774	0	2015-05-13 09:13:29.662774	0	\N	\N	\N	ru	ru	89484b14b36a8d5329426a3d944d2983	\N	0
10	\N	2015-05-13 09:13:29.662774	0	2015-05-13 09:13:29.662774	0	\N	\N	\N	hu	hu	18bd9197cb1d833bc352f47535c00320	\N	0
\.


--
-- Data for Name: mywms_user_mywms_role; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_user_mywms_role (mywms_user_id, roles_id) FROM stdin;
0	0
1	0
6	0
2	0
7	0
8	0
9	0
10	0
\.


--
-- Data for Name: mywms_zone; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_zone (id, additionalcontent, created, entity_lock, modified, version, name, client_id) FROM stdin;
\.


--
-- Name: seqentities; Type: SEQUENCE SET; Schema: public; Owner: jboss
--

SELECT pg_catalog.setval('seqentities', 1, false);


--
-- Name: los_avisreq_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_avisreq
    ADD CONSTRAINT los_avisreq_pkey PRIMARY KEY (id);


--
-- Name: los_bom_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_bom
    ADD CONSTRAINT los_bom_pkey PRIMARY KEY (id);


--
-- Name: los_customerorder_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_customerorder
    ADD CONSTRAINT los_customerorder_pkey PRIMARY KEY (id);


--
-- Name: los_customerpos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_customerpos
    ADD CONSTRAINT los_customerpos_pkey PRIMARY KEY (id);


--
-- Name: los_fixassgn_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_fixassgn
    ADD CONSTRAINT los_fixassgn_pkey PRIMARY KEY (id);


--
-- Name: los_goodsreceipt_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_goodsreceipt
    ADD CONSTRAINT los_goodsreceipt_pkey PRIMARY KEY (id);


--
-- Name: los_grrposition_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT los_grrposition_pkey PRIMARY KEY (id);


--
-- Name: los_itemdata_number_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_itemdata_number
    ADD CONSTRAINT los_itemdata_number_pkey PRIMARY KEY (id);


--
-- Name: los_jasperreport_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_jasperreport
    ADD CONSTRAINT los_jasperreport_pkey PRIMARY KEY (id);


--
-- Name: los_locationcluster_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_locationcluster
    ADD CONSTRAINT los_locationcluster_pkey PRIMARY KEY (id);


--
-- Name: los_orderreceipt_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_orderreceipt
    ADD CONSTRAINT los_orderreceipt_pkey PRIMARY KEY (id);


--
-- Name: los_orderreceiptpos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_orderreceiptpos
    ADD CONSTRAINT los_orderreceiptpos_pkey PRIMARY KEY (id);


--
-- Name: los_orderstrat_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_orderstrat
    ADD CONSTRAINT los_orderstrat_pkey PRIMARY KEY (id);


--
-- Name: los_outpos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_outpos
    ADD CONSTRAINT los_outpos_pkey PRIMARY KEY (id);


--
-- Name: los_outreq_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_outreq
    ADD CONSTRAINT los_outreq_pkey PRIMARY KEY (id);


--
-- Name: los_pickingorder_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_pickingorder
    ADD CONSTRAINT los_pickingorder_pkey PRIMARY KEY (id);


--
-- Name: los_pickingpos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_pickingpos
    ADD CONSTRAINT los_pickingpos_pkey PRIMARY KEY (id);


--
-- Name: los_pickingunitload_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_pickingunitload
    ADD CONSTRAINT los_pickingunitload_pkey PRIMARY KEY (id);


--
-- Name: los_pickreceipt_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_pickreceipt
    ADD CONSTRAINT los_pickreceipt_pkey PRIMARY KEY (id);


--
-- Name: los_pickreceiptpos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_pickreceiptpos
    ADD CONSTRAINT los_pickreceiptpos_pkey PRIMARY KEY (id);


--
-- Name: los_rack_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_rack
    ADD CONSTRAINT los_rack_pkey PRIMARY KEY (id);


--
-- Name: los_replenishorder_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_replenishorder
    ADD CONSTRAINT los_replenishorder_pkey PRIMARY KEY (id);


--
-- Name: los_sequencenumber_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_sequencenumber
    ADD CONSTRAINT los_sequencenumber_pkey PRIMARY KEY (classname);


--
-- Name: los_serviceconf_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_serviceconf
    ADD CONSTRAINT los_serviceconf_pkey PRIMARY KEY (id);


--
-- Name: los_sllabel_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_sllabel
    ADD CONSTRAINT los_sllabel_pkey PRIMARY KEY (id);


--
-- Name: los_stockrecord_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_stockrecord
    ADD CONSTRAINT los_stockrecord_pkey PRIMARY KEY (id);


--
-- Name: los_stocktaking_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_stocktaking
    ADD CONSTRAINT los_stocktaking_pkey PRIMARY KEY (id);


--
-- Name: los_stocktakingorder_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_stocktakingorder
    ADD CONSTRAINT los_stocktakingorder_pkey PRIMARY KEY (id);


--
-- Name: los_stocktakingrecord_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_stocktakingrecord
    ADD CONSTRAINT los_stocktakingrecord_pkey PRIMARY KEY (id);


--
-- Name: los_storagelocationtype_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storagelocationtype
    ADD CONSTRAINT los_storagelocationtype_pkey PRIMARY KEY (id);


--
-- Name: los_storagereq_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storagereq
    ADD CONSTRAINT los_storagereq_pkey PRIMARY KEY (id);


--
-- Name: los_storagestrat_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storagestrat
    ADD CONSTRAINT los_storagestrat_pkey PRIMARY KEY (id);


--
-- Name: los_storloc_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT los_storloc_pkey PRIMARY KEY (id);


--
-- Name: los_sulabel_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_sulabel
    ADD CONSTRAINT los_sulabel_pkey PRIMARY KEY (id);


--
-- Name: los_sysprop_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_sysprop
    ADD CONSTRAINT los_sysprop_pkey PRIMARY KEY (id);


--
-- Name: los_typecapacityconstraint_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_typecapacityconstraint
    ADD CONSTRAINT los_typecapacityconstraint_pkey PRIMARY KEY (id);


--
-- Name: los_ul_record_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_ul_record
    ADD CONSTRAINT los_ul_record_pkey PRIMARY KEY (id);


--
-- Name: los_uladvice_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_uladvice
    ADD CONSTRAINT los_uladvice_pkey PRIMARY KEY (id);


--
-- Name: los_uladvicepos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_uladvicepos
    ADD CONSTRAINT los_uladvicepos_pkey PRIMARY KEY (id);


--
-- Name: los_workingarea_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_workingarea
    ADD CONSTRAINT los_workingarea_pkey PRIMARY KEY (id);


--
-- Name: los_workingareapos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_workingareapos
    ADD CONSTRAINT los_workingareapos_pkey PRIMARY KEY (id);


--
-- Name: mywms_area_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_area
    ADD CONSTRAINT mywms_area_pkey PRIMARY KEY (id);


--
-- Name: mywms_clearingitem_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_clearingitem
    ADD CONSTRAINT mywms_clearingitem_pkey PRIMARY KEY (id);


--
-- Name: mywms_client_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_client
    ADD CONSTRAINT mywms_client_pkey PRIMARY KEY (id);


--
-- Name: mywms_document_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_document
    ADD CONSTRAINT mywms_document_pkey PRIMARY KEY (id);


--
-- Name: mywms_itemdata_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_itemdata
    ADD CONSTRAINT mywms_itemdata_pkey PRIMARY KEY (id);


--
-- Name: mywms_itemunit_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_itemunit
    ADD CONSTRAINT mywms_itemunit_pkey PRIMARY KEY (id);


--
-- Name: mywms_logitem_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_logitem
    ADD CONSTRAINT mywms_logitem_pkey PRIMARY KEY (id);


--
-- Name: mywms_lot_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_lot
    ADD CONSTRAINT mywms_lot_pkey PRIMARY KEY (id);


--
-- Name: mywms_pluginconfiguration_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_pluginconfiguration
    ADD CONSTRAINT mywms_pluginconfiguration_pkey PRIMARY KEY (id);


--
-- Name: mywms_request_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_request
    ADD CONSTRAINT mywms_request_pkey PRIMARY KEY (id);


--
-- Name: mywms_role_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_role
    ADD CONSTRAINT mywms_role_pkey PRIMARY KEY (id);


--
-- Name: mywms_stockunit_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_stockunit
    ADD CONSTRAINT mywms_stockunit_pkey PRIMARY KEY (id);


--
-- Name: mywms_unitload_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_unitload
    ADD CONSTRAINT mywms_unitload_pkey PRIMARY KEY (id);


--
-- Name: mywms_unitloadtype_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_unitloadtype
    ADD CONSTRAINT mywms_unitloadtype_pkey PRIMARY KEY (id);


--
-- Name: mywms_user_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_user
    ADD CONSTRAINT mywms_user_pkey PRIMARY KEY (id);


--
-- Name: mywms_zone_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_zone
    ADD CONSTRAINT mywms_zone_pkey PRIMARY KEY (id);


--
-- Name: uk_1a7ijcl2mx7esj2rmykadasir; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_serviceconf
    ADD CONSTRAINT uk_1a7ijcl2mx7esj2rmykadasir UNIQUE (service, client_id, servkey);


--
-- Name: uk_2l2tatdxct1bhv2yrtmp1lvdj; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_document
    ADD CONSTRAINT uk_2l2tatdxct1bhv2yrtmp1lvdj UNIQUE (name, client_id);


--
-- Name: uk_32ljdcfc9texdfh39rjouem4k; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_locationcluster
    ADD CONSTRAINT uk_32ljdcfc9texdfh39rjouem4k UNIQUE (name);


--
-- Name: uk_3yp785ptpbjt85294dvimodx8; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_fixassgn
    ADD CONSTRAINT uk_3yp785ptpbjt85294dvimodx8 UNIQUE (assignedlocation_id);


--
-- Name: uk_434w4iped8p64det9y3yqaia; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_pluginconfiguration
    ADD CONSTRAINT uk_434w4iped8p64det9y3yqaia UNIQUE (client_id, pluginname, plugin_mode);


--
-- Name: uk_48pkipun1pytmies0wei11bhm; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_user
    ADD CONSTRAINT uk_48pkipun1pytmies0wei11bhm UNIQUE (name);


--
-- Name: uk_4c8kfow6cdbi4y69giy0ded2k; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storagelocationtype
    ADD CONSTRAINT uk_4c8kfow6cdbi4y69giy0ded2k UNIQUE (sltname);


--
-- Name: uk_4jqlc53dlk1fcbifr0e3026vc; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_pickingorder
    ADD CONSTRAINT uk_4jqlc53dlk1fcbifr0e3026vc UNIQUE (number);


--
-- Name: uk_52fpfm3f0o24ueleh5jbrf6tn; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_request
    ADD CONSTRAINT uk_52fpfm3f0o24ueleh5jbrf6tn UNIQUE (request_nr);


--
-- Name: uk_59qpxx4gt30r9bfn6dakqt2av; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_goodsreceipt
    ADD CONSTRAINT uk_59qpxx4gt30r9bfn6dakqt2av UNIQUE (gr_number);


--
-- Name: uk_6c1or9ji7c4gb11goh9db9wfu; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_area
    ADD CONSTRAINT uk_6c1or9ji7c4gb11goh9db9wfu UNIQUE (name, client_id);


--
-- Name: uk_6yyotbpw7edc76ejucc4mflf2; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_role
    ADD CONSTRAINT uk_6yyotbpw7edc76ejucc4mflf2 UNIQUE (name);


--
-- Name: uk_7lyjpsmkph4vq3mgt9i0g3477; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_lot
    ADD CONSTRAINT uk_7lyjpsmkph4vq3mgt9i0g3477 UNIQUE (name, itemdata_id);


--
-- Name: uk_8tcoe23qui9q3ancbhx662iqb; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_sysprop
    ADD CONSTRAINT uk_8tcoe23qui9q3ancbhx662iqb UNIQUE (client_id, syskey, workstation);


--
-- Name: uk_ad3jp6ads3ydnqv5fi6rf0v4s; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_stocktaking
    ADD CONSTRAINT uk_ad3jp6ads3ydnqv5fi6rf0v4s UNIQUE (stocktakingnumber);


--
-- Name: uk_bqervqvxqu8g61pvemek2oy4u; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_orderstrat
    ADD CONSTRAINT uk_bqervqvxqu8g61pvemek2oy4u UNIQUE (name, client_id);


--
-- Name: uk_c9yikd8kvlu4t45xg8hfbu899; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_replenishorder
    ADD CONSTRAINT uk_c9yikd8kvlu4t45xg8hfbu899 UNIQUE (number);


--
-- Name: uk_eifxf4sfjlf4wf3qw8tudqkpf; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_unitloadtype
    ADD CONSTRAINT uk_eifxf4sfjlf4wf3qw8tudqkpf UNIQUE (name);


--
-- Name: uk_emolvlq25quglbkx8qd5s8vhh; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT uk_emolvlq25quglbkx8qd5s8vhh UNIQUE (name);


--
-- Name: uk_f1g8indilnpf8kx5ad2q6myud; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_workingareapos
    ADD CONSTRAINT uk_f1g8indilnpf8kx5ad2q6myud UNIQUE (workingarea_id, cluster_id);


--
-- Name: uk_f55eo4r08ia2f8ysflwioea4y; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storagereq
    ADD CONSTRAINT uk_f55eo4r08ia2f8ysflwioea4y UNIQUE (number);


--
-- Name: uk_ghgin3lnjeaowpuf0qpvp0rw4; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_client
    ADD CONSTRAINT uk_ghgin3lnjeaowpuf0qpvp0rw4 UNIQUE (name);


--
-- Name: uk_hsiwho7ytejho6qjdjcq1rmjk; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_typecapacityconstraint
    ADD CONSTRAINT uk_hsiwho7ytejho6qjdjcq1rmjk UNIQUE (storagelocationtype_id, unitloadtype_id);


--
-- Name: uk_ipgs95k1hra5lkxrrmuq2d9he; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_uladvice
    ADD CONSTRAINT uk_ipgs95k1hra5lkxrrmuq2d9he UNIQUE (number);


--
-- Name: uk_ksw3yn2ea5uhk3mc5r4n1n9m7; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_customerorder
    ADD CONSTRAINT uk_ksw3yn2ea5uhk3mc5r4n1n9m7 UNIQUE (number);


--
-- Name: uk_l5qltujupqeptdgmsglnpbct1; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_uladvice
    ADD CONSTRAINT uk_l5qltujupqeptdgmsglnpbct1 UNIQUE (labelid);


--
-- Name: uk_m342eni5ncgt1j39ea6rrwsua; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_workingarea
    ADD CONSTRAINT uk_m342eni5ncgt1j39ea6rrwsua UNIQUE (name);


--
-- Name: uk_mdlrq6f4y98wpcvv0kjx6hgj; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_customerpos
    ADD CONSTRAINT uk_mdlrq6f4y98wpcvv0kjx6hgj UNIQUE (client_id, number);


--
-- Name: uk_ml4dwtkudihkygkjkiin11npg; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_outreq
    ADD CONSTRAINT uk_ml4dwtkudihkygkjkiin11npg UNIQUE (number);


--
-- Name: uk_n23h3mdt25nno8wl6c9armsqr; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_client
    ADD CONSTRAINT uk_n23h3mdt25nno8wl6c9armsqr UNIQUE (cl_nr);


--
-- Name: uk_njlpjtxw566a1yycr6fgd18u4; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_rack
    ADD CONSTRAINT uk_njlpjtxw566a1yycr6fgd18u4 UNIQUE (rname, client_id);


--
-- Name: uk_ocoj9ehw8n2sdpc5p09t71dbb; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_avisreq
    ADD CONSTRAINT uk_ocoj9ehw8n2sdpc5p09t71dbb UNIQUE (advicenumber);


--
-- Name: uk_paed8her271at4xkb1ae8w0kf; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_itemdata
    ADD CONSTRAINT uk_paed8her271at4xkb1ae8w0kf UNIQUE (client_id, item_nr);


--
-- Name: uk_pdjgcqlreqny7lsy5ip7jo43r; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_unitload
    ADD CONSTRAINT uk_pdjgcqlreqny7lsy5ip7jo43r UNIQUE (labelid);


--
-- Name: uk_qrejohv6ou22mnsdephm1mp60; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storagestrat
    ADD CONSTRAINT uk_qrejohv6ou22mnsdephm1mp60 UNIQUE (name);


--
-- Name: uk_r8h2wt65kgwdfsbwbgawfyeer; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_zone
    ADD CONSTRAINT uk_r8h2wt65kgwdfsbwbgawfyeer UNIQUE (name, client_id);


--
-- Name: uk_r9j71doodmxi2ri7gaam7jd7l; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_jasperreport
    ADD CONSTRAINT uk_r9j71doodmxi2ri7gaam7jd7l UNIQUE (name, client_id);


--
-- Name: uk_rx5p8i1ehibhrmfhfvxqmknm3; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_bom
    ADD CONSTRAINT uk_rx5p8i1ehibhrmfhfvxqmknm3 UNIQUE (parent_id, child_id);


--
-- Name: uk_shxdy9bmv8aaqcp8uyehtr6rq; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_itemdata_number
    ADD CONSTRAINT uk_shxdy9bmv8aaqcp8uyehtr6rq UNIQUE (number, itemdata_id);


--
-- Name: uk_sjpcgr70c1b14n5fj1bwgj2fu; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT uk_sjpcgr70c1b14n5fj1bwgj2fu UNIQUE (positionnumber);


--
-- Name: fk_130f7t27b32qhv34x9rmigatn; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreceiptpos
    ADD CONSTRAINT fk_130f7t27b32qhv34x9rmigatn FOREIGN KEY (receipt_id) REFERENCES los_pickreceipt(id);


--
-- Name: fk_136624cp6n0n5u6p0anwjg23a; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_avisreq
    ADD CONSTRAINT fk_136624cp6n0n5u6p0anwjg23a FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk_14ypmi1k2iash7t1ubev5dtas; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_itemdata
    ADD CONSTRAINT fk_14ypmi1k2iash7t1ubev5dtas FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_1dy042vb1gqwbbumis13h6cet; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_replenishorder
    ADD CONSTRAINT fk_1dy042vb1gqwbbumis13h6cet FOREIGN KEY (operator_id) REFERENCES mywms_user(id);


--
-- Name: fk_1oodxblhpckoyw8v99pb9qd51; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_unitload
    ADD CONSTRAINT fk_1oodxblhpckoyw8v99pb9qd51 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_1qq9xemkedpxlc7xaxr2cw2uo; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_replenishorder
    ADD CONSTRAINT fk_1qq9xemkedpxlc7xaxr2cw2uo FOREIGN KEY (requestedlocation_id) REFERENCES los_storloc(id);


--
-- Name: fk_2a6uponck83dr84ie2aw1wi9b; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingunitload
    ADD CONSTRAINT fk_2a6uponck83dr84ie2aw1wi9b FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_2atg4yebbrflop5en1xsvx91y; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvice
    ADD CONSTRAINT fk_2atg4yebbrflop5en1xsvx91y FOREIGN KEY (relatedadvice_id) REFERENCES los_avisreq(id);


--
-- Name: fk_2rxaado1x885ocxl36hllj936; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_itemdata
    ADD CONSTRAINT fk_2rxaado1x885ocxl36hllj936 FOREIGN KEY (defultype_id) REFERENCES mywms_unitloadtype(id);


--
-- Name: fk_3yp785ptpbjt85294dvimodx8; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_fixassgn
    ADD CONSTRAINT fk_3yp785ptpbjt85294dvimodx8 FOREIGN KEY (assignedlocation_id) REFERENCES los_storloc(id);


--
-- Name: fk_4hga11ayl6r72egfb4xl3c20r; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_unitload
    ADD CONSTRAINT fk_4hga11ayl6r72egfb4xl3c20r FOREIGN KEY (storagelocation_id) REFERENCES los_storloc(id);


--
-- Name: fk_4kf680cdagiqc3iq5p9l0kfp4; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_replenishorder
    ADD CONSTRAINT fk_4kf680cdagiqc3iq5p9l0kfp4 FOREIGN KEY (lot_id) REFERENCES mywms_lot(id);


--
-- Name: fk_51n126rr6vt4huvr1j8fyq549; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingpos
    ADD CONSTRAINT fk_51n126rr6vt4huvr1j8fyq549 FOREIGN KEY (pickingorder_id) REFERENCES los_pickingorder(id);


--
-- Name: fk_53f7ppwcwe4h0yylvrfelux7w; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreceipt
    ADD CONSTRAINT fk_53f7ppwcwe4h0yylvrfelux7w FOREIGN KEY (id) REFERENCES mywms_document(id);


--
-- Name: fk_54fk6llpmvin3avdf0uifvxwr; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_workingareapos
    ADD CONSTRAINT fk_54fk6llpmvin3avdf0uifvxwr FOREIGN KEY (cluster_id) REFERENCES los_locationcluster(id);


--
-- Name: fk_5pq174elkqu0875yor3bnprtt; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_request
    ADD CONSTRAINT fk_5pq174elkqu0875yor3bnprtt FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_5vq9h4yubp1e04423ifip3ppv; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_replenishorder
    ADD CONSTRAINT fk_5vq9h4yubp1e04423ifip3ppv FOREIGN KEY (stockunit_id) REFERENCES mywms_stockunit(id);


--
-- Name: fk_5wovrjjy9ocjcgqfksdmmkodd; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_jasperreport
    ADD CONSTRAINT fk_5wovrjjy9ocjcgqfksdmmkodd FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_637vvkl8tp58mmqgqj3mdermo; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingorder
    ADD CONSTRAINT fk_637vvkl8tp58mmqgqj3mdermo FOREIGN KEY (strategy_id) REFERENCES los_orderstrat(id);


--
-- Name: fk_68kqbao07jore36o3e83pvdac; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvicepos
    ADD CONSTRAINT fk_68kqbao07jore36o3e83pvdac FOREIGN KEY (unitloadadvice_id) REFERENCES los_uladvice(id);


--
-- Name: fk_6iu26onngx8e6a4fr1qhswdiq; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingunitload
    ADD CONSTRAINT fk_6iu26onngx8e6a4fr1qhswdiq FOREIGN KEY (pickingorder_id) REFERENCES los_pickingorder(id);


--
-- Name: fk_6kssimx3jloe6lh419r7gfy18; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_bom
    ADD CONSTRAINT fk_6kssimx3jloe6lh419r7gfy18 FOREIGN KEY (parent_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk_6rmt63k21bc45m2va3re1a86v; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk_6rmt63k21bc45m2va3re1a86v FOREIGN KEY (area_id) REFERENCES mywms_area(id);


--
-- Name: fk_6ve1bn3rex56rglr8xeterc4d; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_stockrecord
    ADD CONSTRAINT fk_6ve1bn3rex56rglr8xeterc4d FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_7bcrglw1jqm6ryg4i9wx4w2an; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingorder
    ADD CONSTRAINT fk_7bcrglw1jqm6ryg4i9wx4w2an FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_7ex4ci2v36v1yer7dvvvccdyj; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_stocktakingorder
    ADD CONSTRAINT fk_7ex4ci2v36v1yer7dvvvccdyj FOREIGN KEY (stocktaking_id) REFERENCES los_stocktaking(id);


--
-- Name: fk_7g670uvl24xusdtcs83gy7yhr; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storagereq
    ADD CONSTRAINT fk_7g670uvl24xusdtcs83gy7yhr FOREIGN KEY (destination_id) REFERENCES los_storloc(id);


--
-- Name: fk_7lo99vpft3x9a5a4139wl9isv; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingpos
    ADD CONSTRAINT fk_7lo99vpft3x9a5a4139wl9isv FOREIGN KEY (picktounitload_id) REFERENCES los_pickingunitload(id);


--
-- Name: fk_7sgb82flevf1vwt4rwsnwqjjh; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_logitem
    ADD CONSTRAINT fk_7sgb82flevf1vwt4rwsnwqjjh FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_85ffoaf3o9m7flxq324f46xo1; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk_85ffoaf3o9m7flxq324f46xo1 FOREIGN KEY (cluster_id) REFERENCES los_locationcluster(id);


--
-- Name: fk_8by4srayjumajd85uk9erxeec; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvicepos
    ADD CONSTRAINT fk_8by4srayjumajd85uk9erxeec FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_922n1k9u65oev39aw870j8bn5; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_lot
    ADD CONSTRAINT fk_922n1k9u65oev39aw870j8bn5 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_93sv79j1bbe2cqkswwoe3sryy; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_customerorder
    ADD CONSTRAINT fk_93sv79j1bbe2cqkswwoe3sryy FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_97109qj5x304yfy7bu7bvhbh2; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_typecapacityconstraint
    ADD CONSTRAINT fk_97109qj5x304yfy7bu7bvhbh2 FOREIGN KEY (unitloadtype_id) REFERENCES mywms_unitloadtype(id);


--
-- Name: fk_97cf20tqdcnkmbm7fa39aqt4y; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_replenishorder
    ADD CONSTRAINT fk_97cf20tqdcnkmbm7fa39aqt4y FOREIGN KEY (destination_id) REFERENCES los_storloc(id);


--
-- Name: fk_9w8pqnnlu1qps727bcfvedt2a; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_pluginconfiguration
    ADD CONSTRAINT fk_9w8pqnnlu1qps727bcfvedt2a FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_9wcovwxiehc3m6qmourf51yoj; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_itemunit
    ADD CONSTRAINT fk_9wcovwxiehc3m6qmourf51yoj FOREIGN KEY (baseunit_id) REFERENCES mywms_itemunit(id);


--
-- Name: fk_9yvl792jj096ypw47e5868evw; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvice
    ADD CONSTRAINT fk_9yvl792jj096ypw47e5868evw FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_a6jq257y1sy7hds4wu5kr4vnm; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_sysprop
    ADD CONSTRAINT fk_a6jq257y1sy7hds4wu5kr4vnm FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_ac9oo03pkmumii68a95afuumj; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_serviceconf
    ADD CONSTRAINT fk_ac9oo03pkmumii68a95afuumj FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_ajch7flpoc7qs3uc6ah43ed62; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_customerpos
    ADD CONSTRAINT fk_ajch7flpoc7qs3uc6ah43ed62 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_alggbthaao9shflps5x86grgm; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk_alggbthaao9shflps5x86grgm FOREIGN KEY (rack_id) REFERENCES los_rack(id);


--
-- Name: fk_amswid6we3ym57fojfuqwcu45; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderstrat
    ADD CONSTRAINT fk_amswid6we3ym57fojfuqwcu45 FOREIGN KEY (defaultdestination_id) REFERENCES los_storloc(id);


--
-- Name: fk_avqboyarw7gnxl757cry48slb; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderreceipt
    ADD CONSTRAINT fk_avqboyarw7gnxl757cry48slb FOREIGN KEY (id) REFERENCES mywms_document(id);


--
-- Name: fk_awcs6iuc0axq7vkfjdqaa0ppu; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk_awcs6iuc0axq7vkfjdqaa0ppu FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_b545jd5xoe053wey53jorwsqt; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_itemdata
    ADD CONSTRAINT fk_b545jd5xoe053wey53jorwsqt FOREIGN KEY (zone_id) REFERENCES mywms_zone(id);


--
-- Name: fk_b86m7r1t910xoum3hw9s6vyl2; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_goodsreceipt
    ADD CONSTRAINT fk_b86m7r1t910xoum3hw9s6vyl2 FOREIGN KEY (operator_id) REFERENCES mywms_user(id);


--
-- Name: fk_bboid4k8cgaya4p9gx4ppbjhj; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_outreq
    ADD CONSTRAINT fk_bboid4k8cgaya4p9gx4ppbjhj FOREIGN KEY (operator_id) REFERENCES mywms_user(id);


--
-- Name: fk_bur1f0gldpj7a4vtg3jvfdhbe; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT fk_bur1f0gldpj7a4vtg3jvfdhbe FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_c8r1disprpf8pa0kx9g51cmth; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_customerorder
    ADD CONSTRAINT fk_c8r1disprpf8pa0kx9g51cmth FOREIGN KEY (destination_id) REFERENCES los_storloc(id);


--
-- Name: fk_crewwou4hu6pclml7qc3r224m; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_stockunit
    ADD CONSTRAINT fk_crewwou4hu6pclml7qc3r224m FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_d0me94ors6djvprbgtaw61uco; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvicepos
    ADD CONSTRAINT fk_d0me94ors6djvprbgtaw61uco FOREIGN KEY (lot_id) REFERENCES mywms_lot(id);


--
-- Name: fk_dg5kvwoefx6xb5y6qw07c4u13; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_typecapacityconstraint
    ADD CONSTRAINT fk_dg5kvwoefx6xb5y6qw07c4u13 FOREIGN KEY (storagelocationtype_id) REFERENCES los_storagelocationtype(id);


--
-- Name: fk_dka6b9a9ma82xfbxs1wcfgvhr; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingpos
    ADD CONSTRAINT fk_dka6b9a9ma82xfbxs1wcfgvhr FOREIGN KEY (customerorderposition_id) REFERENCES los_customerpos(id);


--
-- Name: fk_dkijwsa9nwom5yy1y84ftk3nn; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_itemdata_number
    ADD CONSTRAINT fk_dkijwsa9nwom5yy1y84ftk3nn FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk_du57jxrmnskys7fhsm9ifxkig; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_itemdata
    ADD CONSTRAINT fk_du57jxrmnskys7fhsm9ifxkig FOREIGN KEY (handlingunit_id) REFERENCES mywms_itemunit(id);


--
-- Name: fk_duhl69nyvlqseyg5jn7ob5pbs; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvice
    ADD CONSTRAINT fk_duhl69nyvlqseyg5jn7ob5pbs FOREIGN KEY (unitloadtype_id) REFERENCES mywms_unitloadtype(id);


--
-- Name: fk_edxhx7qmcxv8t8dl1pi9pd7qd; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_bom
    ADD CONSTRAINT fk_edxhx7qmcxv8t8dl1pi9pd7qd FOREIGN KEY (child_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk_ehdfbhdxxli6f8k7gagqgm1nu; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT fk_ehdfbhdxxli6f8k7gagqgm1nu FOREIGN KEY (goodsreceipt_id) REFERENCES los_goodsreceipt(id);


--
-- Name: fk_eyuhlds1idjue9bt3ic12j6vi; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderreceiptpos
    ADD CONSTRAINT fk_eyuhlds1idjue9bt3ic12j6vi FOREIGN KEY (receipt_id) REFERENCES los_orderreceipt(id);


--
-- Name: fk_f6gvv7xh5jr23pvukq17va4w8; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk_f6gvv7xh5jr23pvukq17va4w8 FOREIGN KEY (type_id) REFERENCES los_storagelocationtype(id);


--
-- Name: fk_fu9rlp10ofosts62rn37lpuc7; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_customerpos
    ADD CONSTRAINT fk_fu9rlp10ofosts62rn37lpuc7 FOREIGN KEY (order_id) REFERENCES los_customerorder(id);


--
-- Name: fk_g64scbxylnibnwgcq7y8oqbr6; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT fk_g64scbxylnibnwgcq7y8oqbr6 FOREIGN KEY (relatedadvice_id) REFERENCES los_avisreq(id);


--
-- Name: fk_g7no3mxnrsxols29wm5d10cr6; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_user
    ADD CONSTRAINT fk_g7no3mxnrsxols29wm5d10cr6 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_gl1i1heemgxcp9n5xm5qx17jw; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_replenishorder
    ADD CONSTRAINT fk_gl1i1heemgxcp9n5xm5qx17jw FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_gp13erauhm4jy7iiaf2q4maew; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_replenishorder
    ADD CONSTRAINT fk_gp13erauhm4jy7iiaf2q4maew FOREIGN KEY (requestedrack_id) REFERENCES los_rack(id);


--
-- Name: fk_h37peta7gs3hq4jan4ppyu6r7; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_customerpos
    ADD CONSTRAINT fk_h37peta7gs3hq4jan4ppyu6r7 FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk_hguvp4j4w6a4wb29hbk5kjjeo; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingunitload
    ADD CONSTRAINT fk_hguvp4j4w6a4wb29hbk5kjjeo FOREIGN KEY (unitload_id) REFERENCES mywms_unitload(id);


--
-- Name: fk_ho705f7xm4vxrgxdy8i3dsw25; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_avisreq
    ADD CONSTRAINT fk_ho705f7xm4vxrgxdy8i3dsw25 FOREIGN KEY (lot_id) REFERENCES mywms_lot(id);


--
-- Name: fk_hppd4sdp7kli4nefqlxu9re73; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storagestrat
    ADD CONSTRAINT fk_hppd4sdp7kli4nefqlxu9re73 FOREIGN KEY (zone_id) REFERENCES mywms_zone(id);


--
-- Name: fk_igt8tbkvpvcc3m2mx6ng3umea; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_customerpos
    ADD CONSTRAINT fk_igt8tbkvpvcc3m2mx6ng3umea FOREIGN KEY (lot_id) REFERENCES mywms_lot(id);


--
-- Name: fk_j44d37d213iecfbv9odra8g67; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvicepos
    ADD CONSTRAINT fk_j44d37d213iecfbv9odra8g67 FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk_j5ibey76d6m0tv9antwbxb2c0; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_goodsreceipt
    ADD CONSTRAINT fk_j5ibey76d6m0tv9antwbxb2c0 FOREIGN KEY (goodsinlocation_id) REFERENCES los_storloc(id);


--
-- Name: fk_jmeh8r6wh3tox725bfqx7l7c2; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_avisreq
    ADD CONSTRAINT fk_jmeh8r6wh3tox725bfqx7l7c2 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_jpnw26uxyeq2atbqp9m9wo8jn; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingpos
    ADD CONSTRAINT fk_jpnw26uxyeq2atbqp9m9wo8jn FOREIGN KEY (strategy_id) REFERENCES los_orderstrat(id);


--
-- Name: fk_jsid3q73fchdor7xoqyfaiqvx; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_outpos
    ADD CONSTRAINT fk_jsid3q73fchdor7xoqyfaiqvx FOREIGN KEY (goodsoutrequest_id) REFERENCES los_outreq(id);


--
-- Name: fk_kaciu0belofcq7dbq2r7b1oq0; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_user_mywms_role
    ADD CONSTRAINT fk_kaciu0belofcq7dbq2r7b1oq0 FOREIGN KEY (roles_id) REFERENCES mywms_role(id);


--
-- Name: fk_kdvp08l6s8rkccyroqqpyjfxv; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingpos
    ADD CONSTRAINT fk_kdvp08l6s8rkccyroqqpyjfxv FOREIGN KEY (lotpicked_id) REFERENCES mywms_lot(id);


--
-- Name: fk_kfgfdk9fxu88u9fano6omagy4; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_outreq
    ADD CONSTRAINT fk_kfgfdk9fxu88u9fano6omagy4 FOREIGN KEY (customerorder_id) REFERENCES los_customerorder(id);


--
-- Name: fk_kk2nsc552ktp91sx1u0v91ofe; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_unitload
    ADD CONSTRAINT fk_kk2nsc552ktp91sx1u0v91ofe FOREIGN KEY (type_id) REFERENCES mywms_unitloadtype(id);


--
-- Name: fk_klo6kt8khoi1p3000obnw9yyu; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT fk_klo6kt8khoi1p3000obnw9yyu FOREIGN KEY (operator_id) REFERENCES mywms_user(id);


--
-- Name: fk_kmnpsoj1cpequhlcstnmiggrp; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT fk_kmnpsoj1cpequhlcstnmiggrp FOREIGN KEY (stockunit_id) REFERENCES mywms_stockunit(id);


--
-- Name: fk_kufr8yv66188js6gtuo1lq1ti; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk_kufr8yv66188js6gtuo1lq1ti FOREIGN KEY (zone_id) REFERENCES mywms_zone(id);


--
-- Name: fk_l7va4kr8pqp7fei9w3pq9ryu1; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_workingareapos
    ADD CONSTRAINT fk_l7va4kr8pqp7fei9w3pq9ryu1 FOREIGN KEY (workingarea_id) REFERENCES los_workingarea(id);


--
-- Name: fk_ld9jgvv5vsbl3ivq828e9n6m7; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_stocktakingrecord
    ADD CONSTRAINT fk_ld9jgvv5vsbl3ivq828e9n6m7 FOREIGN KEY (stocktakingorder_id) REFERENCES los_stocktakingorder(id);


--
-- Name: fk_ltdnny9x5afypoudst3xn0ki5; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingpos
    ADD CONSTRAINT fk_ltdnny9x5afypoudst3xn0ki5 FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk_m9trernv5qlwfhfo0r3mra8cj; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_goodsreceipt_los_avisreq
    ADD CONSTRAINT fk_m9trernv5qlwfhfo0r3mra8cj FOREIGN KEY (los_goodsreceipt_id) REFERENCES los_goodsreceipt(id);


--
-- Name: fk_mc5hm24i682cb3wdhhuijnvfl; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingpos
    ADD CONSTRAINT fk_mc5hm24i682cb3wdhhuijnvfl FOREIGN KEY (pickfromstockunit_id) REFERENCES mywms_stockunit(id);


--
-- Name: fk_mrgibufynmr5ht5ci3pl6pggs; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk_mrgibufynmr5ht5ci3pl6pggs FOREIGN KEY (currenttcc) REFERENCES los_typecapacityconstraint(id);


--
-- Name: fk_mxi0q9s0yv4qewo54ejthtr0u; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_clearingitem
    ADD CONSTRAINT fk_mxi0q9s0yv4qewo54ejthtr0u FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_na80f6x7re3py1dxy86iu79ed; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_outpos
    ADD CONSTRAINT fk_na80f6x7re3py1dxy86iu79ed FOREIGN KEY (source_id) REFERENCES mywms_unitload(id);


--
-- Name: fk_o4602pdlixeb598sh829lvd9h; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_outreq
    ADD CONSTRAINT fk_o4602pdlixeb598sh829lvd9h FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_o8ukym12is74hg3pyocb0yc70; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_area
    ADD CONSTRAINT fk_o8ukym12is74hg3pyocb0yc70 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_o9vq0nm3ccwx1p7e3u8u4q6cj; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_document
    ADD CONSTRAINT fk_o9vq0nm3ccwx1p7e3u8u4q6cj FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_oenr4tgr11ij76j0g9y3sypxg; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_user_mywms_role
    ADD CONSTRAINT fk_oenr4tgr11ij76j0g9y3sypxg FOREIGN KEY (mywms_user_id) REFERENCES mywms_user(id);


--
-- Name: fk_oggftxiqb67aj5iesdievo042; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_sllabel
    ADD CONSTRAINT fk_oggftxiqb67aj5iesdievo042 FOREIGN KEY (id) REFERENCES mywms_document(id);


--
-- Name: fk_ors6m1tghquna7ndlb7wur0io; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_zone
    ADD CONSTRAINT fk_ors6m1tghquna7ndlb7wur0io FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_p58yyogfogmidi5oodybjfpeu; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_itemdata_number
    ADD CONSTRAINT fk_p58yyogfogmidi5oodybjfpeu FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_pfhkpqs7kd2gajhw2m1ixee2j; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_goodsreceipt_los_avisreq
    ADD CONSTRAINT fk_pfhkpqs7kd2gajhw2m1ixee2j FOREIGN KEY (assignedadvices_id) REFERENCES los_avisreq(id);


--
-- Name: fk_ptkjaig5j9at5brfjlpo1av2j; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_rack
    ADD CONSTRAINT fk_ptkjaig5j9at5brfjlpo1av2j FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_pun85eb2sqopmxvggdc3adfhq; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_outreq
    ADD CONSTRAINT fk_pun85eb2sqopmxvggdc3adfhq FOREIGN KEY (outlocation_id) REFERENCES los_storloc(id);


--
-- Name: fk_pvnvku8h5vpw2fhbbii2tcpji; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storagereq
    ADD CONSTRAINT fk_pvnvku8h5vpw2fhbbii2tcpji FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_q42vcywt7qfulojmcyr43iivc; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingorder
    ADD CONSTRAINT fk_q42vcywt7qfulojmcyr43iivc FOREIGN KEY (operator_id) REFERENCES mywms_user(id);


--
-- Name: fk_qg8cupcbjj9efvo9uab28o6ks; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingpos
    ADD CONSTRAINT fk_qg8cupcbjj9efvo9uab28o6ks FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_qplxtuq5bk6d24h1m01xfpdy8; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_replenishorder
    ADD CONSTRAINT fk_qplxtuq5bk6d24h1m01xfpdy8 FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk_ragoiuxtml9fkukvfgf4m6x0v; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_fixassgn
    ADD CONSTRAINT fk_ragoiuxtml9fkukvfgf4m6x0v FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk_rmo4pkod81eqao9dp69bnxhmc; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickingorder
    ADD CONSTRAINT fk_rmo4pkod81eqao9dp69bnxhmc FOREIGN KEY (destination_id) REFERENCES los_storloc(id);


--
-- Name: fk_rqvhfrr8or1k0m9cwm5idvjpb; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderstrat
    ADD CONSTRAINT fk_rqvhfrr8or1k0m9cwm5idvjpb FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_rssjymt81gwmy3dnvbfx4ys1l; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_stockunit
    ADD CONSTRAINT fk_rssjymt81gwmy3dnvbfx4ys1l FOREIGN KEY (lot_id) REFERENCES mywms_lot(id);


--
-- Name: fk_rx6tor6md073bcuiibc81kc82; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_stockunit
    ADD CONSTRAINT fk_rx6tor6md073bcuiibc81kc82 FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk_s2hdwgcthb3c2tm0a37w0so6a; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_customerorder
    ADD CONSTRAINT fk_s2hdwgcthb3c2tm0a37w0so6a FOREIGN KEY (strategy_id) REFERENCES los_orderstrat(id);


--
-- Name: fk_s341pty5ctuw95akotxewfvig; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderreceiptpos
    ADD CONSTRAINT fk_s341pty5ctuw95akotxewfvig FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_s72np1wb34rw5ui65rf74yq08; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_stockunit
    ADD CONSTRAINT fk_s72np1wb34rw5ui65rf74yq08 FOREIGN KEY (unitload_id) REFERENCES mywms_unitload(id);


--
-- Name: fk_s82luwfngpx8rdidm0u300nca; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_lot
    ADD CONSTRAINT fk_s82luwfngpx8rdidm0u300nca FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk_t0lx7lasdejvdbcri6eyc6acl; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_unitload
    ADD CONSTRAINT fk_t0lx7lasdejvdbcri6eyc6acl FOREIGN KEY (carrierunitload_id) REFERENCES mywms_unitload(id);


--
-- Name: fk_t9xgvfmd63so9j7o3ynurxgtj; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_goodsreceipt
    ADD CONSTRAINT fk_t9xgvfmd63so9j7o3ynurxgtj FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_tjdx5g7umgn60qx8fvpfa3nag; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storagereq
    ADD CONSTRAINT fk_tjdx5g7umgn60qx8fvpfa3nag FOREIGN KEY (unitload_id) REFERENCES mywms_unitload(id);


--
-- Name: fk_tkjcu9jv0ijq85hy1xeftrexg; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_ul_record
    ADD CONSTRAINT fk_tkjcu9jv0ijq85hy1xeftrexg FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk_x9k9rrv8hw9o8olpdkskfsbf; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_sulabel
    ADD CONSTRAINT fk_x9k9rrv8hw9o8olpdkskfsbf FOREIGN KEY (id) REFERENCES mywms_document(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

