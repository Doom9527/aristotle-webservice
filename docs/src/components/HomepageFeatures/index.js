import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

const FeatureList = [
  {
    title: '灵活的知识图谱API',
    Svg: require('@site/static/img/logo.svg').default,
    description: (
      <>
        Aristotle Webservice 基于Neo4j提供标准化的知识图谱API，轻松集成到你的业务系统。
      </>
    ),
  },
  {
    title: '可扩展与高性能',
    Svg: require('@site/static/img/undraw_docusaurus_tree.svg').default,
    description: (
      <>
        支持大规模数据与高并发访问，缓存与分布式设计助力企业级应用。
      </>
    ),
  },
  {
    title: '开源与开放',
    Svg: require('@site/static/img/undraw_docusaurus_react.svg').default,
    description: (
      <>
        完全开源，Apache 2.0协议，欢迎社区共建与二次开发。
      </>
    ),
  },
];

function Feature({Svg, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
