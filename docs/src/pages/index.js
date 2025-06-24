import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import HomepageFeatures from '@site/src/components/HomepageFeatures';
import useBaseUrl from '@docusaurus/useBaseUrl';

import Heading from '@theme/Heading';
import styles from './index.module.css';

function HomepageHeader() {
  const {siteConfig} = useDocusaurusContext();
  return (
      <header className={clsx('hero hero--primary', styles.heroBanner)}>
        <div className="container">
          <img src={useBaseUrl('/img/logo.svg')} alt="logo" style={{width: 90, marginBottom: 16}} />
        <Heading as="h1" className="hero__title">
          Aristotle
        </Heading>
        <p className="hero__subtitle">基于Neo4j，开箱即用的知识图谱API服务，灵活可扩展</p>
        <div className={styles.buttons}>
          <Link
            className={clsx('button', styles['button--main'])}
            to="/docs/setup">
            快速开始
          </Link>
          <Link
            className={clsx('button', styles['button--github'])}
            to="https://github.com/Doom9527/aristotle-webservice"
            target="_blank">
            GitHub 仓库
          </Link>
        </div>
        <div className={styles.license}>
          <span>项目基于 <b>Apache 2.0</b> 开源协议发布</span>
        </div>
      </div>
    </header>
  );
}

export default function Home() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout
      title={`Hello from ${siteConfig.title}`}
      description="Description will go into a meta tag in <head />">
      <HomepageHeader />
      <main>
        <HomepageFeatures />
      </main>
    </Layout>
  );
}
