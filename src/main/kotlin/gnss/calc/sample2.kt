package gnss.calc

/*
 * Created by aimozg on 15.11.2018.
 * Confidential unless published on GitHub
 */
fun main(args: Array<String>) {
/*
 Reference ephemerides:

 1 18 02 02 12 00  0.0-2.665724605322D-05-1.932676241267D-12 0.000000000000D+00
    1.600000000000D+01-9.084375000000D+01 4.675908993335D-09-1.235858202843D+00
   -4.725530743599D-06 7.310447515920D-03 3.166496753693D-06 5.153672981262D+03
    4.752000000000D+05 2.197921276093D-07-6.200229577820D-01-3.166496753693D-08
    9.701322546567D-01 3.217187500000D+02 6.156644786339D-01-8.377491589329D-09
   -2.528676712465D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00 5.587935447693D-09 1.600000000000D+01
    4.679400000000D+05 0.000000000000D+00
 2 18 02 02 12 00  0.0 2.114670351148D-04-1.045918907039D-11 0.000000000000D+00
    8.800000000000D+01-8.500000000000D+01 5.342722708690D-09-8.438900988842D-01
   -4.062429070473D-06 1.774149155244D-02 3.281980752945D-06 5.153668294907D+03
    4.752000000000D+05-1.695007085800D-07-6.787306415013D-01 2.831220626831D-07
    9.491144447806D-01 3.125312500000D+02-1.901351840765D+00-8.906085646743D-09
   -2.421529365915D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-2.048909664154D-08 8.800000000000D+01
    4.679400000000D+05 0.000000000000D+00
 3 18 02 02 12 00  0.0 1.353677362204D-06 7.162270776462D-12 0.000000000000D+00
    1.400000000000D+01-7.009375000000D+01 4.322322944006D-09-2.191055342501D+00
   -3.511086106300D-06 1.192225143313D-03 1.247785985470D-05 5.153572456360D+03
    4.752000000000D+05 1.117587089539D-08 4.223461186740D-01-3.911554813385D-08
    9.610408390713D-01 1.393125000000D+02 5.163539044641D-01-7.851755690069D-09
    1.075044775423D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00 2.328306436539D-09 1.400000000000D+01
    4.679400000000D+05 4.000000000000D+00
 4 18 02 02 12 00  0.0 3.576837480068D-05 2.273736754432D-12 0.000000000000D+00
    1.050000000000D+02 5.221875000000D+01 4.941991704754D-09 2.176821563596D+00
    2.706423401833D-06 9.490477503277D-03 1.427344977856D-05 5.153681364059D+03
    4.752000000000D+05-8.381903171539D-08-2.692405948281D+00-2.142041921616D-07
    9.804379697825D-01 1.130312500000D+02 8.931931416281D-01-7.552814373923D-09
   -1.642925517853D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 1.000000000000D+00-1.955777406693D-08 1.050000000000D+02
    4.679400000000D+05 4.000000000000D+00
 5 18 02 02 12 00  0.0-1.534633338451D-05 1.023181539495D-12 0.000000000000D+00
    8.900000000000D+01-6.093750000000D+01 4.667694231131D-09 1.732044113858D+00
   -3.084540367126D-06 5.288508371450D-03 1.293420791626D-05 5.153642385483D+03
    4.752000000000D+05-1.154839992523D-07 4.020942283357D-01 6.332993507385D-08
    9.479571624723D-01 1.263125000000D+02 6.260383189288D-01-8.021048714113D-09
    3.571577408823D-11 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.071020960808D-08 8.900000000000D+01
    4.679400000000D+05 0.000000000000D+00
 6 18 02 02 11 59 44.0 4.148087464273D-04-2.273736754432D-13 0.000000000000D+00
    1.000000000000D+01-8.987500000000D+01 4.674837406071D-09-9.861039495191D-01
   -4.731118679047D-06 1.060983864591D-03 3.553926944733D-06 5.153565252304D+03
    4.751840000000D+05-5.215406417847D-08-6.283588815096D-01-7.264316082001D-08
    9.698601899298D-01 3.175937500000D+02-1.244207023657D+00-8.399635653689D-09
   -2.957266098669D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00 4.656612873077D-09 1.000000000000D+01
    4.679100000000D+05 4.000000000000D+00
 7 18 02 02 12 00  0.0 2.426835708320D-04-5.570655048359D-12 0.000000000000D+00
    1.300000000000D+01-4.456250000000D+01 4.783770712891D-09-1.917517503935D+00
   -2.453103661537D-06 1.115404185839D-02 6.018206477165D-06 5.153687425613D+03
    4.752000000000D+05 6.519258022308D-08 2.535277220655D+00-1.545995473862D-07
    9.595991947468D-01 2.615625000000D+02-2.545113331763D+00-8.120695227376D-09
   -3.985880236979D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.117587089539D-08 1.300000000000D+01
    4.679400000000D+05 0.000000000000D+00
 8 18 02 02 12 00  0.0-8.976692333817D-05-1.364242052659D-12 0.000000000000D+00
    5.800000000000D+01 1.031250000000D+02 4.515902318758D-09 1.370203600834D+00
    5.379319190979D-06 3.288748557679D-03 4.686415195465D-06 5.153756420135D+03
    4.752000000000D+05 3.166496753693D-08-1.684823909507D+00-4.842877388000D-08
    9.683133530179D-01 2.907187500000D+02-5.737539032674D-01-8.302845522223D-09
    4.193031755140D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00 5.587935447693D-09 5.800000000000D+01
    4.679400000000D+05 4.000000000000D+00
 9 18 02 02 12 00  0.0 4.980601370335D-04 2.955857780762D-12 0.000000000000D+00
    9.000000000000D+00 5.087500000000D+01 4.930562624850D-09 1.195854990309D+00
    2.527609467506D-06 1.171770272776D-03 7.234513759613D-06 5.153686103821D+03
    4.752000000000D+05-5.215406417847D-08 1.456801018981D+00-4.470348358154D-08
    9.525175564995D-01 2.340937500000D+02 1.740749440493D+00-8.270344409311D-09
    4.550189669494D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00 1.396983861923D-09 9.000000000000D+00
    4.679400000000D+05 0.000000000000D+00
10 18 02 02 12 00  0.0 1.546866260469D-04 5.115907697473D-12 0.000000000000D+00
    8.000000000000D+00-7.725000000000D+01 4.256963226368D-09 3.016994146259D+00
   -3.930181264877D-06 3.225559485145D-03 1.414120197296D-05 5.153677730560D+03
    4.752000000000D+05-2.421438694000D-08 4.188206148397D-01-2.235174179077D-08
    9.611592198652D-01 1.071562500000D+02-2.841968558689D+00-7.784967337443D-09
    8.464638018291D-11 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00 1.862645149231D-09 8.000000000000D+00
    4.679400000000D+05 4.000000000000D+00
11 18 02 02 11 59 44.0-7.354775443673D-04-7.958078640513D-13 0.000000000000D+00
    1.000000000000D+01-5.593750000000D+00 6.178114464461D-09-1.941951226063D+00
    1.322478055954D-07 1.675238227472D-02-1.862645149231D-08 5.153755249023D+03
    4.751840000000D+05 1.639127731323D-07-1.039486609967D+00 3.799796104431D-07
    9.037883082622D-01 3.466562500000D+02 1.737109770532D+00-9.085021623889D-09
    4.285892821199D-11 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.210719347000D-08 1.000000000000D+01
    4.679100000000D+05 4.000000000000D+00
12 18 02 02 12 00  0.0 3.523915074766D-04-1.932676241267D-12 0.000000000000D+00
    9.000000000000D+00 5.943750000000D+01 3.498717093819D-09-2.500513475555D+00
    3.037974238396D-06 6.886322400533D-03 1.312233507633D-05 5.153656417847D+03
    4.752000000000D+05 4.656612873077D-08-2.658486979854D+00-5.960464477539D-08
    9.878907664386D-01 1.403437500000D+02 9.171920874291D-01-7.369949983627D-09
   -8.036048909643D-11 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.257285475731D-08 9.000000000000D+00
    4.679400000000D+05 4.000000000000D+00
13 18 02 02 12 00  0.0-9.553041309118D-05-1.136868377216D-13 0.000000000000D+00
    9.500000000000D+01 4.912500000000D+01 4.809486142676D-09-5.078113274658D-01
    2.650544047356D-06 3.699141787365D-03 5.986541509628D-06 5.153652101517D+03
    4.752000000000D+05-2.421438694000D-08 1.586852889924D+00 4.284083843231D-08
    9.682919753960D-01 2.661250000000D+02 1.627873319138D+00-8.289274155970D-09
    4.057311986383D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.117587089539D-08 9.500000000000D+01
    4.679400000000D+05 0.000000000000D+00
14 18 02 02 11 59 44.0-9.335530921817D-05-5.684341886081D-13 0.000000000000D+00
    8.000000000000D+00 4.665625000000D+01 4.948063292431D-09 6.122143692087D-01
    2.514570951462D-06 9.304480743594D-03 7.048249244690D-06 5.153728567123D+03
    4.751840000000D+05-6.332993507385D-08 1.545697481647D+00 3.352761268616D-08
    9.608481274098D-01 2.459062500000D+02-1.955826452525D+00-8.462495593164D-09
    4.068026471238D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-9.778887033463D-09 8.000000000000D+00
    4.679100000000D+05 4.000000000000D+00
15 18 02 02 12 00  0.0-3.571831621230D-04 1.136868377216D-13 0.000000000000D+00
    9.000000000000D+00 6.028125000000D+01 5.653449708376D-09 9.317092642878D-02
    3.285706043243D-06 1.015942543745D-02 7.228925824165D-06 5.153679292679D+03
    4.752000000000D+05 6.891787052155D-08 1.388882909828D+00 1.899898052216D-07
    9.281255845903D-01 2.200937500000D+02 6.644809431213D-01-8.777865545539D-09
    5.010922787818D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.071020960808D-08 9.000000000000D+00
    4.679400000000D+05 0.000000000000D+00
16 18 02 02 12 00  0.0 3.301631659269D-05-6.821210263297D-13 0.000000000000D+00
    6.100000000000D+01 4.884375000000D+01 3.468715981114D-09 1.867763026616D+00
    2.650544047356D-06 9.806319256313D-03 1.403130590916D-05 5.153589733124D+03
    4.752000000000D+05-1.564621925354D-07-2.639606603010D+00 4.097819328308D-08
    9.883259743267D-01 1.250625000000D+02 4.623415704279D-01-7.553171421648D-09
   -2.039370616824D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.071020960808D-08 6.100000000000D+01
    4.679400000000D+05 0.000000000000D+00
17 18 02 02 12 00  0.0-1.263441517949D-04 2.955857780762D-12 0.000000000000D+00
    9.800000000000D+01 8.796875000000D+01 4.288392752017D-09 8.608979171795D-01
    4.561617970467D-06 1.237459876575D-02 4.520639777184D-06 5.153756008148D+03
    4.752000000000D+05-2.160668373108D-07-1.623836200667D+00 3.166496753693D-08
    9.816669511688D-01 3.019687500000D+02-1.795487177088D+00-8.410707685869D-09
    3.603721487888D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.800000000000D+00 0.000000000000D+00-1.117587089539D-08 9.800000000000D+01
    4.679400000000D+05 4.000000000000D+00
19 18 02 02 12 00  0.0-4.597539082170D-04 2.501110429876D-12 0.000000000000D+00
    5.100000000000D+01 9.237500000000D+01 4.349467008780D-09-2.404141565872D+00
    4.641711711884D-06 9.705691481940D-03 3.311783075333D-06 5.153698690414D+03
    4.752000000000D+05 1.359730958939D-07-1.576563021127D+00 3.166496753693D-08
    9.790901731157D-01 3.214375000000D+02 1.119067650642D+00-8.128909989580D-09
    4.850201906770D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.536682248116D-08 5.100000000000D+01
    4.679400000000D+05 4.000000000000D+00
20 18 02 02 11 59 44.0 4.998836666346D-04 1.023181539495D-12 0.000000000000D+00
    6.000000000000D+00-8.556250000000D+01 5.021280724549D-09-2.047985218119D-01
   -4.349276423454D-06 4.496817942709D-03 1.293607056141D-05 5.153790098190D+03
    4.751840000000D+05-4.470348358154D-08 3.166997253005D-01-9.313225746155D-09
    9.283467339163D-01 1.132812500000D+02 1.777672246407D+00-8.314989585756D-09
   -1.857220280344D-11 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-8.381903171539D-09 6.000000000000D+00
    4.679100000000D+05 4.000000000000D+00
21 18 02 02 12 00  0.0-4.349867813289D-04 4.320099833421D-12 0.000000000000D+00
    1.100000000000D+01-1.069062500000D+02 5.219860099714D-09-2.962600271484D+00
   -5.589798092842D-06 2.437966549769D-02 1.858919858932D-06 5.153774316788D+03
    4.752000000000D+05 4.135072231293D-07-6.710713407921D-01 1.341104507446D-07
    9.426168136710D-01 3.285312500000D+02-1.599418838352D+00-8.422136765773D-09
   -2.221521161472D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.024454832077D-08 1.100000000000D+01
    4.679400000000D+05 0.000000000000D+00
22 18 02 02 12 00  0.0-2.778200432658D-04-1.296029950026D-11 0.000000000000D+00
    1.100000000000D+01-7.615625000000D+01 5.222003274241D-09 2.862874739138D-01
   -3.816559910774D-06 7.244198583066D-03 1.239031553268D-05 5.153607963562D+03
    4.752000000000D+05-1.098960638046D-07 3.670441843150D-01-5.401670932770D-08
    9.250386343379D-01 1.267500000000D+02-1.618400639306D+00-8.421779718049D-09
    2.500104116787D-11 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.769512891769D-08 1.100000000000D+01
    4.679400000000D+05 4.000000000000D+00
23 18 02 02 11 59 44.0-2.190293744206D-04 0.000000000000D+00 0.000000000000D+00
    1.000000000000D+00 5.046875000000D+01 5.278791181951D-09-4.034937450084D-01
    2.669170498848D-06 1.193476293702D-02 7.152557373047D-06 5.153597709656D+03
    4.751840000000D+05-9.313225746155D-09 1.456246340425D+00 1.471489667892D-07
    9.433561885604D-01 2.305937500000D+02-2.399955390678D+00-8.615359092801D-09
    4.189460167670D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-2.002343535423D-08 1.000000000000D+00
    4.679100000000D+05 4.000000000000D+00
24 18 02 02 12 00  0.0-4.554865881801D-05-4.547473508865D-13 0.000000000000D+00
    5.000000000000D+01-4.206250000000D+01 5.317007278904D-09-9.835946865982D-01
   -2.214685082436D-06 6.758775445633D-03 5.744397640228D-06 5.153649496078D+03
    4.752000000000D+05 1.601874828339D-07 2.477514356726D+00 2.048909664154D-08
    9.426027301587D-01 2.584375000000D+02 5.187241784641D-01-8.518926009060D-09
   -5.375224154669D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00 2.793967723846D-09 5.000000000000D+01
    4.679400000000D+05 4.000000000000D+00
25 18 02 02 12 00  0.0-5.322545766830D-04-6.139089236967D-12 0.000000000000D+00
    7.000000000000D+00 5.712500000000D+01 3.654795133201D-09-2.945348085890D+00
    2.874061465263D-06 7.101413561031D-03 1.402199268341D-05 5.153700899124D+03
    4.752000000000D+05 2.421438694000D-08-2.716801469229D+00-7.450580596924D-08
    9.756278995267D-01 1.158125000000D+02 8.077020801480D-01-7.492097608974D-09
   -1.735786653301D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00 5.587935447693D-09 7.000000000000D+00
    4.679400000000D+05 4.000000000000D+00
26 18 02 02 12 00  0.0-2.650860697031D-04 1.375610736432D-11 0.000000000000D+00
    8.500000000000D+01 4.268750000000D+01 4.220175764402D-09 2.843023047596D+00
    2.292916178703D-06 2.629173337482D-03 1.373514533043D-05 5.153611934662D+03
    4.752000000000D+05-3.725290298462D-09-2.736684625214D+00-2.421438694000D-08
    9.572328940323D-01 1.125937500000D+02-6.789519839325D-04-7.848184324644D-09
   -2.435815715796D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00 7.450580596924D-09 8.500000000000D+01
    4.679400000000D+05 0.000000000000D+00
27 18 02 02 12 00  0.0 3.655203618109D-04 1.591615728103D-12 0.000000000000D+00
    8.000000000000D+00 1.130312500000D+02 4.354824056918D-09 1.018333551851D+00
    5.817040801048D-06 5.419513210654D-03 5.062669515610D-06 5.153680721283D+03
    4.752000000000D+05 3.911554813385D-08-1.677266359139D+00 7.450580596924D-09
    9.764754367271D-01 2.887500000000D+02 3.505221338196D-01-8.322132316607D-09
    4.210889692491D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00 1.862645149231D-09 8.000000000000D+00
    4.679400000000D+05 0.000000000000D+00
28 18 02 02 12 00  0.0 6.852098740637D-04 3.296918293927D-12 0.000000000000D+00
    9.300000000000D+01 4.759375000000D+01 3.511217684882D-09 1.832107192607D+00
    2.386048436165D-06 1.978145691101D-02 1.270510256290D-05 5.153675577164D+03
    4.752000000000D+05 2.961605787277D-07-2.635282258130D+00-3.576278686523D-07
    9.871722893340D-01 1.439062500000D+02-1.528417822452D+00-7.344948803742D-09
   -4.143029716675D-11 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.799999952316D+00 0.000000000000D+00-1.117587089539D-08 9.300000000000D+01
    4.679400000000D+05 0.000000000000D+00
29 18 02 02 12 00  0.0 5.023195408285D-04-5.456968210638D-12 0.000000000000D+00
    5.900000000000D+01 1.059062500000D+02 4.234819162008D-09 2.382600944184D+00
    5.545094609261D-06 4.675487289205D-04 3.831461071968D-06 5.153741752625D+03
    4.752000000000D+05 3.539025783539D-08-1.613383668546D+00-3.352761268616D-08
    9.828919548809D-01 3.168437500000D+02 7.273896857629D-01-8.237843296399D-09
    5.082354537222D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.024454832077D-08 5.900000000000D+01
    4.679400000000D+05 0.000000000000D+00
30 18 02 02 12 00  0.0 1.071952283382D-04-3.410605131648D-12 0.000000000000D+00
    8.600000000000D+01-4.512500000000D+01 5.121284729626D-09-1.761848124869D+00
   -2.454966306686D-06 3.067858517170D-03 6.370246410370D-06 5.153569927216D+03
    4.752000000000D+05-2.793967723846D-08 2.570532321903D+00-5.029141902924D-08
    9.452739205559D-01 2.488437500000D+02-3.112301048654D+00-8.394278161461D-09
   -4.932348418585D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00 3.725290298462D-09 8.600000000000D+01
    4.679400000000D+05 0.000000000000D+00
31 18 02 02 12 00  0.0 1.444811932743D-04-2.614797267597D-12 0.000000000000D+00
    6.200000000000D+01-4.321875000000D+01 4.824843635731D-09-2.123673463044D+00
   -2.270564436913D-06 8.829863741994D-03 6.647780537605D-06 5.153593948364D+03
    4.752000000000D+05-9.685754776001D-08 2.547291191074D+00-8.754432201385D-08
    9.642955275107D-01 2.563125000000D+02-1.612779436566D-01-8.238557391849D-09
   -5.046638662520D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00-1.350417733192D-08 6.200000000000D+01
    4.679400000000D+05 0.000000000000D+00
32 18 02 02 12 00  0.0-5.328934639692D-04 3.410605131648D-13 0.000000000000D+00
    1.000000000000D+01 5.178125000000D+01 4.934133990275D-09 1.754693405086D+00
    2.721324563026D-06 1.631684601307D-03 7.525086402893D-06 5.153626552582D+03
    4.752000000000D+05 7.450580596924D-09 1.461219744022D+00 5.401670932770D-08
    9.569304196273D-01 2.328125000000D+02-2.696225804399D+00-8.297488918174D-09
    5.332365105026D-10 1.000000000000D+00 1.986000000000D+03 0.000000000000D+00
    2.000000000000D+00 0.000000000000D+00 4.656612873077D-10 1.000000000000D+01
    4.679400000000D+05 4.000000000000D+00
 */
	val reftime = GnssTime.fromGpsWeek(1986,479646.000)
/*
 Reference observations:

 #RANGECMP,2,96,0,38.5,180,1986,479646.000,00100000,9691,9603;
 38,
 30,20229597.234,0.050,-106307294.777344,0.006, -534.871,51.0,12758.500,08109c04,
 30,20229597.117,0.050, -82836875.433594,0.006, -416.785,47.0,12752.219,01309c0b,
  7,21007845.234,0.050,-110397017.765625,0.006,-2027.465,50.0,16425.375,18109c24,
  7,21007842.906,0.050, -86023641.984375,0.006,-1579.848,42.0,16052.594,11309c2b,
  1,24564793.656,0.113,-129088860.460938,0.031, 3470.941,38.0,  735.719,08109c44,
  1,24564795.117,0.113,-100588735.378906,0.031, 2704.625,37.0,  685.125,01309c4b,
 27,24428878.500,0.113,-128374670.542969,0.020,-3612.375,39.0,14684.906,08109c64,
 27,24428878.781,0.075,-100032201.148438,0.023,-2814.840,39.0,14679.656,01309c6b,
 15,24207764.461,0.050,-127212668.871094,0.014, 2449.289,44.0, 1663.125,18109c84,
 15,24207761.953,0.113, -99126746.937500,0.020, 1908.535,36.0, 1658.594,11309c8b,
  8,21769299.836,0.050,-114398475.949219,0.006,-1937.355,49.0,11618.469,18109ca4,
  8,21769300.336,0.050, -89141702.625000,0.006,-1509.633,43.0,11590.469,11309cab,
 17,25030562.555,0.113,-131536491.613281,0.016, 3738.688,40.0,  293.781,18109cc4,
 17,25030562.664,0.380,-102495982.167969,0.031, 2913.273,27.0,  287.125,11309ccb,
 28,20995584.656,0.050,-110332560.246094,0.006, 1364.742,48.0, 5414.969,18109ce4,
 28,20995582.172,0.050, -85973422.160156,0.006, 1063.430,43.0, 5415.000,11309ceb,
 13,22214966.188,0.050,-116740454.804688,0.008, 1577.805,46.0, 5402.344,08109d24,
 13,22214963.500,0.113, -90966575.250000,0.010, 1229.453,38.0, 5398.094,01309d2b,
 11,22775967.953,0.050,-119688529.941406,0.010, 2718.953,45.0, 4165.063,18109d64,
 11,22775965.688,0.113, -93263779.226563,0.012, 2118.664,37.0, 4137.563,11309d6b,
 54,20899197.883,0.075,-111835847.406250,0.008, 3339.953,48.0, 4095.781,18119e04,
 54,20899200.164,0.113, -86983462.265625,0.002, 2597.738,43.0, 4085.719,00b19e0b,
 44,23258979.047,0.169,-124507164.265625,0.018, 2609.332,42.0, 1049.125,18119e24,
 44,23258980.680,0.113, -96838883.718750,0.002, 2029.480,42.0, 1023.313,10b19e2b,
 43,21741548.320,0.169,-116017071.296875,0.014, -917.609,42.0, 5023.969,18119e44,
 43,21741551.555,0.253, -90235512.562500,0.002, -713.695,37.0, 5024.000,10b19e4b,
 53,21898465.523,0.169,-116977685.835938,0.020, 3439.031,40.0, 3014.656,18119e64,
 53,21898470.063,0.169, -90982670.804688,0.002, 2674.801,38.0, 3004.375,10b19e6b,
 61,20293307.734,0.113,-108517466.085938,0.012, -636.742,45.0, 7834.219,08119e84,
 61,20293308.914,0.380, -84402488.902344,0.004, -495.246,33.0, 7224.344,10b19e8b,
 51,21113763.047,0.075,-112548260.628906,0.010,-2923.816,47.0,16724.500,08119ee4,
 51,21113768.180,0.113, -87537570.656250,0.002,-2274.082,44.0,16718.781,10b19eeb,
 60,22890163.227,0.169,-122446968.171875,0.016,-3775.164,43.0,11243.031,18119f04,
 60,22890167.938,0.253, -95236570.953125,0.002,-2936.242,36.0,11243.031,10b19f0b,
 42,23339124.250,0.253,-124760986.468750,0.027,-4017.094,36.0,12693.563,18119f24,
 42,23339128.109,0.854, -97036343.031250,0.006,-3124.406,27.0,12294.344,10b19f2b,
 52,19282501.766,0.075,-103039884.363281,0.008, -230.586,48.0,10739.031,18119f44,
 52,19282506.883,0.169, -80142154.335938,0.002, -179.348,40.0,10575.344,00b19f4b
*/
/*
 Reference DGPS correction:

 #RTCM1,2,96,0,38.5,180,1986,479646.000,00100000,d18a,9603;
 Rtcm2Frame1:
    102,1,0;
    846.0,0,17,0;
    SCALE-,0,30,-12.3 , 0.06, 88;
    SCALE-,0, 7,-13.04,-0.18, 60;
    SCALE-,0, 1,  0.0 ,-0.2 , 17;
    SCALE-,0,27,-19.76,-0.02,  9;
    SCALE-,0,15,-19.04,-0.04, 10;
    SCALE-,0, 8,  0.0 , 0.06, 62;
    SCALE-,0,17,-30.02, 0.06, 10;
    SCALE-,0,28,-12.38,-0.18, 94;
    SCALE-,0,13,  0.0 ,-0.16,101;
    SCALE-,0,11,-14.54,-0.04, 11;
*/

}