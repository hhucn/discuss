run:
	lein fig

deps:
	yarn install
	lein deps

sass:
	sassc resources/public/css/discuss.sass resources/public/css/discuss.css
	sassc resources/public/css/zeit.sass resources/public/css/zeit.css

min:
	lein do clean, cljsbuild once min

docs:
	lein codox

clean:
	lein clean

build: deps sass